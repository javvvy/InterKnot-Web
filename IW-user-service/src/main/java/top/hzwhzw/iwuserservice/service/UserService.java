package top.hzwhzw.iwuserservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import dto.UserDTO;
import top.hzwhzw.iwuserservice.pojo.User;
import vo.AvatarVO;
import vo.ProfileVO;

import java.util.List;

public interface UserService extends IService<User> {
    Object getProfile();

    Object getCards();

    void equipCard(String cardNo);

    List<AvatarVO> getAvatars();

    void equipAvatar(String avatarNo);

    void updateProfile(UserDTO userDTO);

    ProfileVO getProfileByNo(String userNo);
}
