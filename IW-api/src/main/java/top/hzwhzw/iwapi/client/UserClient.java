package top.hzwhzw.iwapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import vo.UserVO2;

import java.util.List;

// value指定服务在注册中心的名字
@FeignClient(value = "interknot-user", path = "/user")
public interface UserClient {
    // 批量查询用户
    @PostMapping("/batchQueryUsers")
    List<UserVO2> batchQueryUsers(@RequestBody List<Long> ids);
    // 根据id查询用户
    @GetMapping("/{id}")
    UserVO2 queryUserById(@PathVariable Long id);
    // 根据userNo查询用户
    @GetMapping("/userNo/{userNo}")
    UserVO2 queryUserByUserNo(@PathVariable String userNo);
}
