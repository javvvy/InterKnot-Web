package top.hzwhzw.iwuserservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.hzwhzw.iwuserservice.mapper.OpenMapper;
import top.hzwhzw.iwuserservice.pojo.User;
import top.hzwhzw.iwuserservice.service.OpenService;
import vo.UserVO2;

import java.util.List;

@Service
public class OpenServiceImpl extends ServiceImpl<OpenMapper, User> implements OpenService {
    @Override
    public List<UserVO2> batchQueryUsers(List<Long> userIds) {
        List<User> users = this.listByIds(userIds);
        List<UserVO2> voList = users.stream()
                .map(user -> BeanUtil.copyProperties(user, UserVO2.class))
                .toList();
        return voList;
    }
    @Override
    public UserVO2 queryUserById(Long id) {
        User user = this.getById(id);
        if(user == null){
            return null;
        }
        return BeanUtil.copyProperties(user, UserVO2.class);
    }
    @Override
    public UserVO2 queryUserByUserNo(String userNo) {
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserNo, userNo));
        if(user == null){
            return null;
        }
        return BeanUtil.copyProperties(user, UserVO2.class);
    }
}
