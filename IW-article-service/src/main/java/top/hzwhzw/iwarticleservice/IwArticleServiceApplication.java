package top.hzwhzw.iwarticleservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "top.hzwhzw.iwapi.client")
public class IwArticleServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IwArticleServiceApplication.class, args);
    }

}
