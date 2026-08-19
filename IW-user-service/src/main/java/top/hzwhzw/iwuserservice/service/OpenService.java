package top.hzwhzw.iwuserservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import dto.AvatarDTO;
import top.hzwhzw.iwuserservice.pojo.User;
import vo.AvatarVO;
import vo.UserVO;
import vo.UserVO2;

import java.util.List;

public interface OpenService extends IService<User> {
    List<UserVO2> batchQueryUsers(List<Long> userIds);

    UserVO2 queryUserById(Long id);

    UserVO2 queryUserByUserNo(String userNo);

    AvatarVO insertAvatar(AvatarDTO avatar);

    UserVO queryUserVOById(Long id);
}
