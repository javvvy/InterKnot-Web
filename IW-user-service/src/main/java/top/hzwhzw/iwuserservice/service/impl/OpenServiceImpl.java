package top.hzwhzw.iwuserservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dto.AvatarDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.hzwhzw.iwuserservice.mapper.AvatarMapper;
import top.hzwhzw.iwuserservice.mapper.OpenMapper;
import top.hzwhzw.iwuserservice.pojo.Avatar;
import top.hzwhzw.iwuserservice.pojo.User;
import top.hzwhzw.iwuserservice.service.OpenService;
import utils.UserContextHolder;
import vo.AvatarVO;
import vo.UserVO;
import vo.UserVO2;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenServiceImpl extends ServiceImpl<OpenMapper, User> implements OpenService {
    private final AvatarMapper avatarMapper;
    @Override
    public List<UserVO2> batchQueryUsers(List<Long> userIds) {
        List<User> users = this.listByIds(userIds);
        List<UserVO2> voList = users.stream()
                .map(user -> BeanUtil.copyProperties(user, UserVO2.class))
                .toList();
        return voList;
    }
    @Override
    public UserVO2 queryUserById(Long id) {
        User user = this.getById(id);
        if(user == null){
            return null;
        }
        return BeanUtil.copyProperties(user, UserVO2.class);
    }
    @Override
    public UserVO2 queryUserByUserNo(String userNo) {
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserNo, userNo));
        if(user == null){
            return null;
        }
        return BeanUtil.copyProperties(user, UserVO2.class);
    }
    @Override
    public AvatarVO insertAvatar(AvatarDTO avatarDTO) {
        Avatar avatar = new Avatar();
        //TODO 枚举?
        avatar.setAvatarNo("avatar-"+ IdUtil.getSnowflakeNextIdStr());
        avatar.setUrl(avatarDTO.getUrl());
        avatar.setWidth(avatarDTO.getWidth());
        avatar.setHeight(avatarDTO.getHeight());
        avatar.setUserId(UserContextHolder.getUserId());
        avatarMapper.insert(avatar);
        return BeanUtil.copyProperties(avatar, AvatarVO.class);
    }
    @Override
    public UserVO queryUserVOById(Long id) {
        User user = this.getById(id);
        if(user == null){
            return null;
        }
        return BeanUtil.copyProperties(user, UserVO.class);
    }
}
