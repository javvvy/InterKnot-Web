package top.hzwhzw.iwuserservice.controller;


import dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pojo.Result;
import top.hzwhzw.iwuserservice.service.UserService;

@Slf4j
@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    // 获取用户信息
    @GetMapping("/profile")
    public Result getProfile(){
        log.info("获取用户信息");
        return Result.success(userService.getProfile());
    }
    // 获取我的名片列表
    @GetMapping("/cards")
    public Result getCards(){
        log.info("获取我的名片列表");
        return Result.success(userService.getCards());
    }
    // 装备名片
    @PostMapping("/cards/equip")
    public Result equipCard(@RequestParam String cardNo){
        log.info("装备名片: {}", cardNo);
        userService.equipCard(cardNo);
        return Result.successMsg("装备成功");
    }
    // 获取我的头像列表
    @GetMapping("/avatars")
    public Result getAvatars(){
        log.info("获取我的头像列表");
        return Result.success(userService.getAvatars());
    }
    // 装备头像
    @PostMapping("/avatars/equip")
    public Result equipAvatar(@RequestParam String avatarNo){
        log.info("装备头像: {}", avatarNo);
        userService.equipAvatar(avatarNo);
        return Result.successMsg("装备成功");
    }
    //修改用户信息,包括昵称,个人简介,是否隐藏个人简介
    @PostMapping("/profile")
    public Result updateProfile(@RequestBody UserDTO userDTO){
        log.info("修改信息: {}",userDTO);
        userService.updateProfile(userDTO);
        return Result.successMsg("修改成功");
    }
    //获取指定用户资料
    @GetMapping("/profile/{userNo}")
    public Result getProfile(@PathVariable String userNo){
        log.info("获取用户资料: {}", userNo);
        return Result.success(userService.getProfileByNo(userNo));
    }
}
