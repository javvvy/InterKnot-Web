package top.hzwhzw.iwuserservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.hzwhzw.iwuserservice.pojo.User;
import vo.UserVO;

import java.util.List;

public interface OpenService extends IService<User> {
    List<UserVO> batchQueryUsers(List<Long> userIds);

    UserVO queryUserById(Long id);

    UserVO queryUserByUserNo(String userNo);
}
