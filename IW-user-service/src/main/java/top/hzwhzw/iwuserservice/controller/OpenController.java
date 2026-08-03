package top.hzwhzw.iwuserservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.hzwhzw.iwuserservice.service.OpenService;
import vo.UserVO;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class OpenController {
    private final OpenService openService;
    /**
     * 批量查询用户
     */
    @PostMapping("/batchQueryUsers")
    public List<UserVO> batchQueryUsers(@RequestBody List<Long> userIds) {
        log.info("batchQueryUsers: {}", userIds);
        return openService.batchQueryUsers(userIds);
    }
    /**
     * 根据用户ID查询用户
     */
    @GetMapping("/{id}")
    public UserVO queryUserById(@PathVariable Long id) {
        log.info("queryUserById: {}", id);
        return openService.queryUserById(id);
    }
    /**
     * 根据用户编号查询用户
     */
    @GetMapping("/userNo/{userNo}")
    public UserVO queryUserByUserNo(@PathVariable String userNo) {
        log.info("queryUserByUserNo: {}", userNo);
        return openService.queryUserByUserNo(userNo);
    }
}
