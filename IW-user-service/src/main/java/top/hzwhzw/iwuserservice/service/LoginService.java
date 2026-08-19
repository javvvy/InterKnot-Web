package top.hzwhzw.iwuserservice.service;

import dto.LoginDTO;
import dto.RegisterDTO;
import pojo.Result;
import vo.LoginVO;

public interface LoginService {
    Object login(LoginDTO loginDTO);

    Object sendRegisterCode(String email) throws Exception;

    Object register(RegisterDTO user) throws Exception;

    LoginVO renew();
}
