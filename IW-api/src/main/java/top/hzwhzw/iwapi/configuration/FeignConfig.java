package top.hzwhzw.iwapi.configuration;

import feign.Logger;
import feign.RequestInterceptor;
import feign.Retryer;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class FeignConfig {

    // 不使用HttpClient5，使用默认的HttpURLConnection
    // 只需要配置超时和重试即可

    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(100, 1000, 3);
    }

    /**
     * 配置日志级别
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;  // 可选: NONE, BASIC, HEADERS, FULL
    }

    /**
     * 配置请求拦截器（统一添加Header）
     */

//    @Bean
//    public RequestInterceptor requestInterceptor() {
//        return requestTemplate -> {
//            // 添加统一Header
//            requestTemplate.header("X-Request-Id", MDC.get("requestId"));
//            requestTemplate.header("X-Request-Source", MDC.get("source"));
//
//            // 如果需要动态获取Token，可以在这里添加
//            // String token = getTokenFromContext();
//            // requestTemplate.header("Authorization", "Bearer " + token);
//        };
//    }
}
