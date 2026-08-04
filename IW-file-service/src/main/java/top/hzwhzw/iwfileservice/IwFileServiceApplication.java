package top.hzwhzw.iwfileservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "top.hzwhzw.iwapi.client")
public class IwFileServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IwFileServiceApplication.class, args);
    }

}
