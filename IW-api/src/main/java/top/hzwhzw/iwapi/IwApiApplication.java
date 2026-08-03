package top.hzwhzw.iwapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class IwApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(IwApiApplication.class, args);
    }

}
