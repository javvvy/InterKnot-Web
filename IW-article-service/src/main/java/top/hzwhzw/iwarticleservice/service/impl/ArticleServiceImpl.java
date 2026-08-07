package top.hzwhzw.iwarticleservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dto.ArticleLikesDTO;
import dto.ReadsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import top.hzwhzw.iwapi.client.UserClient;
import top.hzwhzw.iwarticleservice.mapper.ArticleMapper;
import top.hzwhzw.iwarticleservice.mapper.CoverMapper;
import top.hzwhzw.iwarticleservice.mapper.LikesMapper;
import top.hzwhzw.iwarticleservice.mapper.ReadsMapper;
import top.hzwhzw.iwarticleservice.pojo.Article;
import top.hzwhzw.iwarticleservice.pojo.ArticleLikes;
import top.hzwhzw.iwarticleservice.pojo.Cover;
import top.hzwhzw.iwarticleservice.pojo.Reads;
import top.hzwhzw.iwarticleservice.service.ArticleService;
import utils.UserContextHolder;
import vo.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    private final ArticleMapper articleMapper;
    private final CoverMapper coverMapper;
    private final ReadsMapper readsMapper;
    private final LikesMapper likesMapper;
    private final UserClient userClient;
    @Override
    public IPage<ArticlePageVO> pageList(Integer pageNum, Integer pageSize) {
        // 1. 创建 Page 对象，传入当前页和每页条数
        Page<Article> page = new Page<>(pageNum, pageSize);
        // 2. 调用分页查询方法，传入 Page 对象
        IPage<Article> articlePage = articleMapper.selectPage(page,null);
        // 如果当前页没有数据，直接返回空结果
        if (articlePage.getRecords().isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }
        // 3. 从文章列表中提取作者ID，去重并转换为作者ID列表
        List<Long> authorIds = articlePage.getRecords().stream()
                .map(Article::getAuthorId)
                .distinct()
                .collect(Collectors.toList());
        //4.批量查询作者信息
        List<UserVO2> users = userClient.batchQueryUsers(authorIds);
        if(users == null||users.isEmpty()){
            return new Page<>(pageNum, pageSize);
        }
        // 转为Map，方便后续快速查找
        Map<Long, UserVO2> userMap = users.stream()
                .collect(Collectors.toMap(UserVO2::getId, java.util.function.Function.identity()));

        // 5. 组装VO：文章 + 作者信息
        List<ArticlePageVO> voList = articlePage.getRecords().stream()
                .map(article -> {
                    ArticlePageVO vo = new ArticlePageVO();
                    BeanUtils.copyProperties(article, vo);

                    // 设置作者信息
                    UserVO2 userVO2 = userMap.get(article.getAuthorId());
                    // 设置封面信息
                    List<Cover> covers = coverMapper.selectList(
                            new LambdaQueryWrapper<Cover>().eq(Cover::getArticleNo, article.getArticleNo())
                    );
                    if(covers !=null){
                        List<CoverVO> coverVOList = covers.stream()
                            .map(cover -> {
                                CoverVO coverVO = new CoverVO();
                                BeanUtils.copyProperties(cover, coverVO);
                                return coverVO;
                            })
                            .collect(Collectors.toList());
                            vo.setCovers(coverVOList);
                    }
                    if (userVO2 != null) {
                        UserVO user=new UserVO();
                        BeanUtils.copyProperties(userVO2, user);
                        vo.setAuthor(user);
                        // 其他需要展示的作者字段...
                    }
                    return vo;
                })
                .collect(Collectors.toList());

        // 6. 构造分页结果（保留原分页信息）
        Page<ArticlePageVO> voPage = new Page<>(pageNum, pageSize);
        voPage.setRecords(voList);
        voPage.setTotal(articlePage.getTotal());

        return voPage;// 该对象里包含了数据列表、总记录数、当前页等所有信息
    }

    @Override
    public ArticleVO detail(String articleNo) {
        Article article = articleMapper.selectOne(
                new LambdaQueryWrapper<Article>().eq(Article::getArticleNo, articleNo)
        );
        if(article == null){
            return null;
        }
        ArticleVO articleVO = new ArticleVO();
        List<Cover> covers = coverMapper.selectList(
                new LambdaQueryWrapper<Cover>().eq(Cover::getArticleNo, articleNo)
        );
        BeanUtils.copyProperties(article, articleVO);
        if(covers !=null){
            List<CoverVO> coverVOList = covers.stream()
                    .map(cover -> {
                        CoverVO coverVO = new CoverVO();
                        BeanUtils.copyProperties(cover, coverVO);
                        return coverVO;
                    })
                    .collect(Collectors.toList());
            articleVO.setCovers(coverVOList);
        }
        UserVO2 userVO2 = userClient.queryUserById(article.getAuthorId());
        if(userVO2 != null){
            UserVO user=new UserVO();
            BeanUtils.copyProperties(userVO2, user);
            articleVO.setAuthor(user);
        }
        return articleVO;
    }
    @Override
    public void view(String articleNo) {
        int rows = articleMapper.update(null,
                Wrappers.<Article>lambdaUpdate()
                        .eq(Article::getArticleNo, articleNo)
                        .setSql("views = views + 1")
        );
    }
    @Override
    public void deleteArticle(String articleNo) {
        Article article = articleMapper.selectOne(
                new LambdaQueryWrapper<Article>().eq(Article::getArticleNo, articleNo)
        );
        //TODO 鉴权
        if(!article.getAuthorId().toString().equals(UserContextHolder.getUserId())){
            throw new IllegalArgumentException("您没有权限删除该文章");
        }
        articleMapper.delete(
                new LambdaQueryWrapper<Article>().eq(Article::getArticleNo, articleNo)
        );
    }
    @Override
    public List<ReadsVO> selectReads(ReadsDTO readsDTO,String userNo) {
        if (readsDTO.getArticleNos() == null || readsDTO.getArticleNos().isEmpty()) {
            throw new IllegalArgumentException("文章编号不能为空");
        }

        // 1. 查询已读记录
        List<Reads> readsList = readsMapper.selectList(
                new LambdaQueryWrapper<Reads>()
                        .in(Reads::getArticleNo, readsDTO.getArticleNos())
                        .eq(Reads::getUserNo, userNo)
        );

        // 2. 构建已读文章编号的 Set
        Set<String> readArticleNos = readsList.stream()
                .map(Reads::getArticleNo)
                .collect(Collectors.toSet());

        // 3. 一次性构建所有结果
        return readsDTO.getArticleNos().stream()
                .map(articleNo -> new ReadsVO(articleNo, readArticleNos.contains(articleNo)))
                .collect(Collectors.toList());
    }
    @Override
    public void markReads(ReadsDTO readsDTO, String userNo) {
        if (readsDTO.getArticleNos() == null || readsDTO.getArticleNos().isEmpty()) {
            throw new IllegalArgumentException("文章编号不能为空");
        }
        // 1. 构建已读记录
        List<Reads> readsList = readsDTO.getArticleNos().stream()
                .map(articleNo -> Reads.builder()
                        .articleNo(articleNo)
                        .userNo(userNo)
                        .build())
                .collect(Collectors.toList());
        // 2. 批量插入已读记录
        readsMapper.insertBatchSomeColumn(readsList);
    }
    @Override
    public void like(String articleNo) {
        // 1. 查询是否点赞过
        boolean liked = liked(UserContextHolder.getUserId(), articleNo);
        if (liked) {
            // 2. 取消点赞
            likesMapper.delete(new LambdaQueryWrapper<ArticleLikes>()
                    .eq(ArticleLikes::getUserId, UserContextHolder.getUserId())
                    .eq(ArticleLikes::getArticleNo, articleNo));
        } else {
            // 2. 点赞
            ArticleLikes articleLikes = new ArticleLikes();
            articleLikes.setUserId(UserContextHolder.getUserId());
            articleLikes.setArticleNo(articleNo);
            likesMapper.insert(articleLikes);
        }
    }
    @Override
    public List<ArticleLikesVO> likes(ArticleLikesDTO articleLikesDTO) {
        if (articleLikesDTO.getArticleNos() == null || articleLikesDTO.getArticleNos().isEmpty()) {
            throw new IllegalArgumentException("文章编号不能为空");
        }
        return articleLikesDTO.getArticleNos().stream()
                .map(articleNo -> new ArticleLikesVO(articleNo, liked(UserContextHolder.getUserId(), articleNo)))
                .collect(Collectors.toList());
    }
    @Override
    public IPage<ArticlePageVO> pageListByUserNo(String userNo, Integer pageNum, Integer pageSize) {
        // 1. 创建 Page 对象，传入当前页和每页条数
        Page<Article> page = new Page<>(pageNum, pageSize);

        UserVO2 userVO2 = userClient.queryUserByUserNo(userNo);
        if(userVO2 == null){
            throw new IllegalArgumentException("用户不存在");
        }
        // 2. 调用分页查询方法，传入 Page 对象
        IPage<Article> articlePage = articleMapper.selectPage(page,new LambdaQueryWrapper<Article>()
                .eq(Article::getAuthorId, UserContextHolder.getUserId())
                .orderByDesc(Article::getCreatedAt)
        );
        // 如果当前页没有数据，直接返回空结果
        if (articlePage.getRecords().isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }
        // 5. 组装VO：文章 + 作者信息
        List<ArticlePageVO> voList = articlePage.getRecords().stream()
                .map(article -> {
                    ArticlePageVO vo = new ArticlePageVO();
                    BeanUtils.copyProperties(article, vo);

                    // 设置作者信息
                    UserVO user = new UserVO();
                    BeanUtils.copyProperties(userVO2, user);
                    vo.setAuthor(user);
                    // 设置封面信息
                    List<Cover> covers = coverMapper.selectList(
                            new LambdaQueryWrapper<Cover>().eq(Cover::getArticleNo, article.getArticleNo())
                    );
                    if(covers !=null){
                        List<CoverVO> coverVOList = covers.stream()
                                .map(cover -> {
                                    CoverVO coverVO = new CoverVO();
                                    BeanUtils.copyProperties(cover, coverVO);
                                    return coverVO;
                                })
                                .collect(Collectors.toList());
                        vo.setCovers(coverVOList);
                    }
                    return vo;
                })
                .collect(Collectors.toList());

        // 6. 构造分页结果（保留原分页信息）
        Page<ArticlePageVO> voPage = new Page<>(pageNum, pageSize);
        voPage.setRecords(voList);
        voPage.setTotal(articlePage.getTotal());

        return voPage;// 该对象里包含了数据列表、总记录数、当前页等所有信息
    }




    /**
     * 查询是否点赞过
     * @param userId 用户编号
     * @param articleNo 文章编号
     * @return 是否点赞过
     */
    private boolean liked(Long userId, String articleNo) {
        return likesMapper.selectOne(
                new LambdaQueryWrapper<ArticleLikes>()
                        .eq(ArticleLikes::getUserId, userId)
                        .eq(ArticleLikes::getArticleNo, articleNo)
        ) != null;
    }
}