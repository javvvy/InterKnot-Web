package top.hzwhzw.iwuserservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dto.LoginDTO;
import dto.RegisterDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pojo.EmailConstant;
import top.hzwhzw.iwuserservice.mapper.AvatarMapper;
import top.hzwhzw.iwuserservice.mapper.CardMapper;
import top.hzwhzw.iwuserservice.mapper.UserMapper;
import top.hzwhzw.iwuserservice.pojo.Avatar;
import top.hzwhzw.iwuserservice.pojo.Card;
import top.hzwhzw.iwuserservice.pojo.RegisterData;
import top.hzwhzw.iwuserservice.pojo.User;
import top.hzwhzw.iwuserservice.service.LoginService;
import top.hzwhzw.iwuserservice.util.EmailUtil;
import utils.JwtUtils;
import utils.UserContextHolder;
import vo.LoginVO;
import vo.RegisterVO;
import vo.UserVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;
    private final EmailUtil emailUtil;
    private final AvatarMapper avatarMapper;
    private final CardMapper cardMapper;

    @Override
    public Object login(LoginDTO loginDTO) {
        //根据email查询用户是否存在
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
        .eq(User::getEmail, loginDTO.getEmail()));
        if(user == null){
            throw new IllegalArgumentException("用户不存在");
        }
        //密码校验
        Boolean isMatch = passwordEncoder.matches(loginDTO.getPassword(), user.getPasswordHash());
        if(!isMatch){
            throw new IllegalArgumentException("密码错误");
        }
        //生成jwt
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", user.getId());
        dataMap.put("role", user.getRole());
        String jwt = JwtUtils.generateJwt(dataMap);
        //返回登录成功
        LoginVO loginVO = new LoginVO();
        loginVO.setJwt(jwt);
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        loginVO.setUser(userVO);
        return loginVO;
    }
    @Override
    public Object sendRegisterCode(String email) throws Exception {
        //发送注册验证码
        // 1. 生成6位验证码
        String code = RandomStringUtils.randomNumeric(EmailConstant.CODE_LENGTH);

        // 2. 存入Redis，绑定邮箱，5分钟过期
        String key = EmailConstant.EMAIL_CODE_PREFIX + email;
        redisTemplate.opsForValue().set(key, code, EmailConstant.CODE_EXPIRE, TimeUnit.SECONDS);

        // 3. 发送邮件到用户邮箱
        emailUtil.sendCodeEmail(email, code);
        RegisterVO registerVO = new RegisterVO();
        registerVO.setEmail(email);
        registerVO.setSent(true);
        registerVO.setExpires(EmailConstant.CODE_EXPIRE);
        registerVO.setCooldown(EmailConstant.CODE_COOLDOWN);
        return registerVO;
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Object register(RegisterDTO registerDTO) throws Exception {
        String key = EmailConstant.EMAIL_CODE_PREFIX + registerDTO.getEmail();
        // 从Redis取出真实验证码
        String realCode = redisTemplate.opsForValue().get(key);
        // 校验验证码
        if (StringUtils.isBlank(realCode) || !realCode.equals(registerDTO.getCode())) {
            throw new IllegalArgumentException("验证码错误或已过期");
        }
        // 校验通过 → 删除验证码，防止重复使用
        redisTemplate.delete(key);
        //入库
        RegisterData registerData = new RegisterData();
        User user=new User();
        //拷贝
        user.setRole("user");
        user.setExp(registerData.getExp());
        user.setLevel(registerData.getLevel());
        user.setProfileHidden(registerData.getProfileHidden());
        user.setBio(registerData.getBio());
        user.setAvatar(registerData.getAvatar());
        user.setUserName(registerData.getUserName());
        user.setNickName(registerData.getNickName());
        user.setUserNo(registerData.getUserNo());
        user.setPasswordHash(passwordEncoder.encode(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setRole("user");
        // 注册用户表
        User hasUser = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, registerDTO.getEmail()));
        if(hasUser != null){
            throw new IllegalArgumentException("邮箱已注册");
        }
        userMapper.insert(user);
        //获取id
        Long userId = user.getId();
        // 注册用户头像表
        //先获取公共头像
        List<Avatar> avatarList = avatarMapper.selectList(new LambdaQueryWrapper<Avatar>()
                .eq(Avatar::getUserId, 0));
        // 为每个头像设置用户ID
        avatarList.forEach(avatar -> {
            avatar.setUserId(userId);
            avatar.setAvatarNo("avatar-"+ IdUtil.getSnowflakeNextIdStr());
        });
        // 注册用户头像表
        int result = avatarMapper.insertBatchSomeColumn(avatarList);
        // 注册用户名片表
        List<Card> cardList = cardMapper.selectList(new LambdaQueryWrapper<Card>()
                .eq(Card::getUserId, 0));
        cardList.forEach(card -> {
            card.setUserId(userId);
            card.setCardNo("card-"+IdUtil.getSnowflakeNextIdStr());
        }  );
        // 注册用户名片表
        cardMapper.insertBatchSomeColumn(cardList);
        Map<String, Object> map = new HashMap<>();
        map.put("id", userId);
        map.put("role", user.getRole());
        String token = JwtUtils.generateJwt(map);
        LoginVO loginVO = new LoginVO();
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        loginVO.setJwt(token);
        loginVO.setUser(userVO);
        return loginVO;
    }
    @Override
    public LoginVO renew() {
        Long id = UserContextHolder.getUserId();
        LoginVO loginVO = new LoginVO();
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("role", UserContextHolder.getUserRole());
        String token = JwtUtils.generateJwt(map);
        loginVO.setJwt(token);
        return loginVO;
    }
}
