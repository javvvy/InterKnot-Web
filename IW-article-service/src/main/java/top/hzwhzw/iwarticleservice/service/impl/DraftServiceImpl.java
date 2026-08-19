package top.hzwhzw.iwarticleservice.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dto.DraftArticleDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.hzwhzw.iwapi.client.UserClient;
import top.hzwhzw.iwarticleservice.mapper.ArticleMapper;
import top.hzwhzw.iwarticleservice.mapper.CoverMapper;
import top.hzwhzw.iwarticleservice.mapper.DraftMapper;
import top.hzwhzw.iwarticleservice.pojo.Article;
import top.hzwhzw.iwarticleservice.pojo.Cover;
import top.hzwhzw.iwarticleservice.pojo.DraftArticle;
import top.hzwhzw.iwarticleservice.service.DraftService;
import utils.UserContextHolder;
import vo.CoverVO;
import vo.DraftArticleVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DraftServiceImpl extends ServiceImpl<DraftMapper, DraftArticle> implements DraftService {
    private final DraftMapper draftMapper;
    private final UserClient userClient;
    private final ArticleMapper articleMapper;
    private final CoverMapper coverMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public DraftArticleVO createDraftArticle(DraftArticleDTO draftArticle) {
        DraftArticle draft = new DraftArticle();
        //插入draftArticle表
        draft.setDraftNo("draft-"+ IdUtil.getSnowflakeNextIdStr());
        draft.setTitle(draftArticle.getTitle());
        draft.setText(draftArticle.getText());
        draft.setHasPublishedVersion(false);
        draft.setCreatedAt(LocalDateTime.now());
        draft.setUpdatedAt(LocalDateTime.now());
        //根据userNo查询用户Id
        Long userId = userClient.queryUserByUserNo(draftArticle.getUserNo()).getId();
        if (userId == null) {
            log.error("userId不能为空");
            throw new IllegalArgumentException("userId不能为空");
        }
        draft.setAuthorId(userId);
        DraftArticleVO draftVO = new DraftArticleVO();
        BeanUtils.copyProperties(draft, draftVO);
        save(draft);
        return draftVO;
        //TODO cover表另外设计
//        UserVO user = userClient.queryUserByUserNo(userNo);
//        if(user != null){
//            draft.setAuthorId(user.getId().toString());
//        }
//        // 先根据draftNo查询draftArticle的id
//        if(draftArticle.getCovers() != null){
//            LambdaQueryWrapper<DraftArticle> lambdaQueryWrapper = new LambdaQueryWrapper<DraftArticle>();
//            lambdaQueryWrapper.select(DraftArticle::getId)
//                    .eq(DraftArticle::getDraftNo, draft.getDraftNo());
//            Long draftId = draftMapper.selectOne(lambdaQueryWrapper).getId();
//            Long draftId = draft.getId();
//            // 插入cover表
//            List<String> coverNos = draftArticle.getCovers();
//            for (String coverNo : coverNos) {
//                Cover cover = new Cover();
//                cover.setCoverNo(coverNo);
//                cover.setArticleId(draftId.toString());
//                cover.setUrl("https://img.interknot.com/cover/");
//                coverMapper.insert(cover);
//            }
//        }
    }
    @Override
    public DraftArticleVO updateDraftArticle(String draftNo, DraftArticleDTO draftArticle) {
        DraftArticle draft = new DraftArticle();
        draft.setTitle(draftArticle.getTitle());
        draft.setText(draftArticle.getText());
        draft.setUpdatedAt(LocalDateTime.now());
        boolean updated = update(draft, new LambdaQueryWrapper<DraftArticle>()
                .eq(DraftArticle::getDraftNo, draftNo));
        if(!updated){
            log.error("更新文章草稿失败");
            throw new IllegalArgumentException("更新文章草稿失败");
        }
        DraftArticleVO draftVO = new DraftArticleVO();
        BeanUtils.copyProperties(draft, draftVO);
        return draftVO;
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishDraftArticle(String draftNo) {
        DraftArticle draft = draftMapper.selectOne(new LambdaQueryWrapper<DraftArticle>()
                .eq(DraftArticle::getDraftNo, draftNo));
        if(draft == null){
            log.error("文章草稿不存在");
            throw new IllegalArgumentException("文章草稿不存在");
        }
        // 插入article表
        Article article = new Article();
        article.setArticleNo("article-"+IdUtil.getSnowflakeNextIdStr());
        article.setAuthorId(draft.getAuthorId());
        article.setTitle(draft.getTitle());
        article.setBody(draft.getText());
        article.setRawBodyText(draft.getText());
        //TODO 设置HTML格式的文本
        article.setTextHtml(draft.getText());
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        articleMapper.insert(article);
        // 更改cover表中的articleNo
        coverMapper.update(new LambdaUpdateWrapper<Cover>()
                        .set(Cover::getArticleNo, article.getArticleNo())
                        .eq(Cover::getArticleNo, draftNo));
        //删除草稿
        draftMapper.deleteById(draft.getId());
    }
    @Override
    public IPage<DraftArticleVO> myArticles(Integer page, Integer pageSize) {
        Long userId = UserContextHolder.getUserId();
        if (userId == null) {
            log.error("userId不能为空");
            throw new IllegalArgumentException("userId不能为空");
        }
        Page<DraftArticle> pageResult = draftMapper.selectPage(new Page<>(page, pageSize),
                new LambdaQueryWrapper<DraftArticle>()
                        .eq(DraftArticle::getAuthorId, userId));
        // 得到 IPage
        return pageResult.convert(article -> {
            DraftArticleVO vo = new DraftArticleVO();
            BeanUtils.copyProperties(article, vo);
            return vo;
        });
    }
    @Override
    public DraftArticleVO getDraftByDraftNo(String draftNo) {
        //TODO 鉴权???
        DraftArticle draft = draftMapper.selectOne(new LambdaQueryWrapper<DraftArticle>()
                .eq(DraftArticle::getDraftNo, draftNo));
        if(draft == null){
            log.error("文章草稿不存在");
            throw new IllegalArgumentException("文章草稿不存在");
        }
        DraftArticleVO vo = new DraftArticleVO();
        BeanUtils.copyProperties(draft, vo);
        // 从cover表中查询封面
        List<Cover> covers = coverMapper.selectList(new LambdaQueryWrapper<Cover>()
                .eq(Cover::getArticleNo, draftNo));
        vo.setCovers(covers.stream().map(cover -> CoverVO.builder()
                .coverNo(cover.getCoverNo())
                .url(cover.getUrl())
                .width(cover.getWidth())
                .height(cover.getHeight())
                .build()).collect(Collectors.toList()));
        return vo;
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteDraftArticle(String draftNo) {
        DraftArticle draft = draftMapper.selectOne(new LambdaQueryWrapper<DraftArticle>()
                .eq(DraftArticle::getDraftNo, draftNo));
        if(draft == null){
            log.error("文章草稿不存在");
            throw new IllegalArgumentException("文章草稿不存在");
        } else if (!draft.getAuthorId().equals(UserContextHolder.getUserId())) {
            log.error("没有权限删除该草稿");
            throw new IllegalArgumentException("没有权限删除该草稿");
        }
        draftMapper.deleteById(draft.getId());
        // 删除封面
        coverMapper.delete(new LambdaQueryWrapper<Cover>()
                .eq(Cover::getArticleNo, draftNo));
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteDraftCover(String draftNo, String coverNo) {
        coverMapper.delete(new LambdaQueryWrapper<Cover>()
                .eq(Cover::getCoverNo, coverNo)
                .eq(Cover::getArticleNo, draftNo));
    }
}
