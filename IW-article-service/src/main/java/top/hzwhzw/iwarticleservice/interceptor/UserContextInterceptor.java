package top.hzwhzw.iwarticleservice.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import utils.UserContextHolder;

@Component
@Slf4j
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userId = request.getHeader("X-User-Id");
        String userRole = request.getHeader("X-User-Role");

        log.info("接收到请求 - URI: {}, X-User-Id: {}, X-User-Role: {}",
                request.getRequestURI(), userId, userRole);
        if (userId != null) {
            UserContextHolder.setUserId(Long.parseLong(userId));
        }
        if (userRole != null) {
            UserContextHolder.setUserRole(userRole);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束后务必清除，防止内存泄漏和线程复用污染
        UserContextHolder.clear();
    }
}