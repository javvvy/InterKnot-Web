package top.hzwhzw.iwapi.client;

import dto.AvatarDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import top.hzwhzw.iwapi.configuration.UserInfoFeignInterceptor;
import vo.AvatarVO;
import vo.UserVO;
import vo.UserVO2;

import java.util.List;

// value指定服务在注册中心的名字
@FeignClient(value = "interknot-user", path = "/user",configuration = UserInfoFeignInterceptor.class)
public interface UserClient {
    // 批量查询用户
    @PostMapping("/batchQueryUsers")
    List<UserVO2> batchQueryUsers(@RequestBody List<Long> ids);
    // 根据id查询用户
    @GetMapping("/{id}")
    UserVO2 queryUserById(@PathVariable("id") Long id);
    // 根据id查询UserVO
    @GetMapping("/vo/{id}")
    UserVO queryUserVOById(@PathVariable("id") Long id);
    // 根据userNo查询用户
    @GetMapping("/userNo/{userNo}")
    UserVO2 queryUserByUserNo(@PathVariable("userNo") String userNo);
    //插入avatar表
    @PostMapping("/insertAvatar")
    AvatarVO insertAvatar(@RequestBody AvatarDTO avatar);
}
