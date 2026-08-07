package top.hzwhzw.iwuserservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.hzwhzw.iwuserservice.pojo.User;
import vo.UserVO2;

import java.util.List;

public interface OpenService extends IService<User> {
    List<UserVO2> batchQueryUsers(List<Long> userIds);

    UserVO2 queryUserById(Long id);

    UserVO2 queryUserByUserNo(String userNo);
}
