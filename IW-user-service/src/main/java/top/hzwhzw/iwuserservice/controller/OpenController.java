package top.hzwhzw.iwuserservice.controller;

import dto.AvatarDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.hzwhzw.iwuserservice.service.OpenService;
import vo.AvatarVO;
import vo.UserVO;
import vo.UserVO2;

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
    public List<UserVO2> batchQueryUsers(@RequestBody List<Long> userIds) {
        log.info("batchQueryUsers: {}", userIds);
        return openService.batchQueryUsers(userIds);
    }
    /**
     * 根据用户ID查询用户
     */
    @GetMapping("/{id}")
    public UserVO2 queryUserById(@PathVariable("id") Long id) {
        log.info("queryUserById: {}", id);
        return openService.queryUserById(id);
    }
    /**
     * 根据用户编号查询用户
     */
    @GetMapping("/userNo/{userNo}")
    public UserVO2 queryUserByUserNo(@PathVariable("userNo") String userNo) {
        log.info("queryUserByUserNo: {}", userNo);
        return openService.queryUserByUserNo(userNo);
    }
    /**
     * 插入头像表
     */
    @PostMapping("/insertAvatar")
    public AvatarVO insertAvatar(@RequestBody AvatarDTO avatar) {
        log.info("insertAvatar: {}", avatar);
        return openService.insertAvatar(avatar);
    }
    /**
     * 根据用户ID查询用户VO
     */
    @GetMapping("/vo/{id}")
    public UserVO queryUserVOById(@PathVariable("id") Long id) {
        log.info("queryUserVOById: {}", id);
        return openService.queryUserVOById(id);
    }
}
