package top.hzwhzw.iwuserservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "top.hzwhzw.iwapi.client")
@MapperScan("top.hzwhzw.iwuserservice.mapper")
public class IwUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IwUserServiceApplication.class, args);
    }

}
