package top.hzwhzw.iwuserservice.controller;

import dto.LoginDTO;
import dto.RegisterDTO;
import dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pojo.Result;
import top.hzwhzw.iwuserservice.pojo.User;
import top.hzwhzw.iwuserservice.service.LoginService;

@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;
    //登录
    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        return Result.success(loginService.login(loginDTO));
    }
    //发送注册验证码
    @PostMapping("/send-register-code")
    public Result sendRegisterCode(@RequestBody LoginDTO loginDTO) throws Exception {
        log.info("发送注册验证码");
        try {
            return Result.success(loginService.sendRegisterCode(loginDTO.getEmail()));
        } catch (Exception e) {
            log.error("发送注册验证码失败: {}", e.getMessage());
            return Result.fail("发送失败：" + e.getMessage());
        }
    }
    //验证码注册
    @PostMapping("/register")
    public Result register(@RequestBody RegisterDTO registerDTO) {
        log.info("验证码注册");
        try {
            return Result.success(loginService.register(registerDTO));
        } catch (Exception e) {
            log.error("注册失败: {}", e.getMessage());
            return Result.fail("注册失败：" + e.getMessage());
        }
    }
    //刷新token
    @PostMapping("/renew")
    public Result renew() {
        log.info("刷新token");
        return Result.success(loginService.renew());
    }
}