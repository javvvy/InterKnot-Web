package top.hzwhzw.iwapi.configuration;

import com.alibaba.cloud.commons.lang.StringUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.seata.core.context.RootContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import utils.UserContextHolder;

@Component
@Slf4j
public class UserInfoFeignInterceptor implements RequestInterceptor {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    @Override
    public void apply(RequestTemplate template) {
        // 从当前请求上下文中获取用户信息
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            log.info("非web请求");
            return; // 非Web请求场景（如定时任务），直接跳过
        }

        HttpServletRequest request = attributes.getRequest();

        // 从UserContextHolder获取用户信息
        Long userId = UserContextHolder.getUserId();
        String userRole = UserContextHolder.getUserRole();

        if (userId == null) {
            userId = 1L; // 系统默认用户ID
            userRole = "ADMIN"; // 系统默认角色
        }
        // 将用户信息设置到Feign请求头中
        if (userId != null) {
            template.header(USER_ID_HEADER, userId.toString());
        }
        if (userRole != null) {
            template.header(USER_ROLE_HEADER, userRole);
        }
        // 从RootContext获取XID
        String xid = RootContext.getXID();
        if (StringUtils.isNotBlank(xid)) {
            template.header(RootContext.KEY_XID, xid);
        }
        log.info("添加用户信息到Feign请求头: {}, {}", userId, userRole);
    }
}