package top.hzwhzw.iwuserservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.hzwhzw.iwuserservice.mapper.AvatarMapper;
import top.hzwhzw.iwuserservice.mapper.CardMapper;
import top.hzwhzw.iwuserservice.mapper.UserMapper;
import top.hzwhzw.iwuserservice.pojo.Avatar;
import top.hzwhzw.iwuserservice.pojo.Card;
import top.hzwhzw.iwuserservice.pojo.User;
import top.hzwhzw.iwuserservice.service.UserService;
import utils.UserContextHolder;
import vo.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final UserMapper userMapper;
    private final CardMapper cardMapper;
    private final AvatarMapper avatarMapper;

    @Override
    public UserVO getProfile() {
        User user = userMapper.selectById(UserContextHolder.getUserId());
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }
    @Override
    public List<CardVO> getCards() {
        // 查询用户自己和公共卡片
        List<Card> cards = cardMapper.selectList(new LambdaQueryWrapper<Card>()
                .eq(Card::getUserId, UserContextHolder.getUserId()));
        List<CardVO> cardVOs = cards.stream().map(card -> {
            CardVO cardVO = new CardVO();
            BeanUtil.copyProperties(card, cardVO);
            return cardVO;
        }).collect(Collectors.toList());
        return cardVOs;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void equipCard(String cardNo) {
        //    查询用户是否有该名片
        Card card = cardMapper.selectOne(new LambdaQueryWrapper<Card>()
                .eq(Card::getCardNo, cardNo)
                .eq(Card::getUserId, UserContextHolder.getUserId()));
        if (card == null) {
            throw new IllegalArgumentException("该名片不存在");
        }
        //将用户的所有装备名片设置为false
        cardMapper.update(new LambdaUpdateWrapper<Card>()
                .set(Card::getEquipped, false)
                .eq(Card::getUserId, UserContextHolder.getUserId()));
        //将该名片设置为装备
        cardMapper.update(new LambdaUpdateWrapper<Card>()
                .set(Card::getEquipped, true)
                .eq(Card::getCardNo, cardNo));
    }
    @Override
    public List<AvatarVO> getAvatars() {
        return avatarMapper.selectList(new LambdaQueryWrapper<Avatar>()
                .eq(Avatar::getUserId, UserContextHolder.getUserId()))
                .stream()
                .map(avatar -> {
                    AvatarVO avatarVO = new AvatarVO();
                    BeanUtil.copyProperties(avatar, avatarVO);
                    return avatarVO;
                }).collect(Collectors.toList());
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void equipAvatar(String avatarNo) {
        //    查询用户是否有该头像
        Avatar avatar = avatarMapper.selectOne(new LambdaQueryWrapper<Avatar>()
                .eq(Avatar::getAvatarNo, avatarNo)
                .eq(Avatar::getUserId, UserContextHolder.getUserId()));
        if (avatar == null) {
            throw new IllegalArgumentException("该头像不存在");
        }
        //将用户的所有装备头像设置为false
        avatarMapper.update(new LambdaUpdateWrapper<Avatar>()
                .set(Avatar::getEquipped, false)
                .eq(Avatar::getUserId, UserContextHolder.getUserId()));
        //将该头像设置为装备
        avatarMapper.update(new LambdaUpdateWrapper<Avatar>()
                .set(Avatar::getEquipped, true)
                .eq(Avatar::getAvatarNo, avatarNo));
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(UserDTO userDTO) {
        User user = userMapper.selectById(UserContextHolder.getUserId());
        BeanUtil.copyProperties(userDTO, user);
        userMapper.updateById(user);
    }
    @Override
    public ProfileVO getProfileByNo(String userNo) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserNo, userNo));
        if (user == null) {
            throw new IllegalArgumentException("该用户不存在");
        }
        ProfileVO profileVO = new ProfileVO();
        BeanUtil.copyProperties(user, profileVO);
        Card card = cardMapper.selectOne(new LambdaQueryWrapper<Card>()
                .eq(Card::getUserId, UserContextHolder.getUserId())
                .eq(Card::getEquipped, true));
        if(card != null){
            CardVO cardVO = new CardVO();
            BeanUtil.copyProperties(card, cardVO);
            profileVO.setCard(cardVO);
        }
        return profileVO;
    }
}
