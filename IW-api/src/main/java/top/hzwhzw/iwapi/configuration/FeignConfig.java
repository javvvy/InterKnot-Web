package top.hzwhzw.iwapi.configuration;

import feign.Logger;
import feign.RequestInterceptor;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(100, 1000, 3);
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    /**
     * 配置请求拦截器 - 注入 UserInfoFeignInterceptor
     */
    @Bean
    public RequestInterceptor requestInterceptor(UserInfoFeignInterceptor userInfoFeignInterceptor) {
        return userInfoFeignInterceptor;
    }
}
