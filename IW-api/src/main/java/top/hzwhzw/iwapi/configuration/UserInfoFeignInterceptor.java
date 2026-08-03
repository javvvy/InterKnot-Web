package top.hzwhzw.iwapi.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import utils.UserContextHolder;

@Configuration
public class UserInfoFeignInterceptor implements RequestInterceptor {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    @Override
    public void apply(RequestTemplate template) {
        // 从当前请求上下文中获取用户信息
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return; // 非Web请求场景（如定时任务），直接跳过
        }

        HttpServletRequest request = attributes.getRequest();

        // 从UserContextHolder获取用户信息
        Long userId = UserContextHolder.getUserId();
        String userRole = UserContextHolder.getUserRole();


        // 将用户信息设置到Feign请求头中
        if (userId != null) {
            template.header(USER_ID_HEADER, userId.toString());
        }
        if (userRole != null) {
            template.header(USER_ROLE_HEADER, userRole);
        }
    }
}