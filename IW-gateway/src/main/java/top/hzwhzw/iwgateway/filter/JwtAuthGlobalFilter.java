package top.hzwhzw.iwgateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import utils.JwtUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/auth/login",
            "/auth/send-register-code",
            "/auth/register"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String token = request.getHeaders().getFirst("token");

        // 1. 完全公开路径（不校验、不注入）
        if (path.contains("/auth") || path.contains("/public")) {
            log.debug("放行公开路径: {}", path);
            return chain.filter(exchange);
        }
        // 放行白名单路径
        if (isWhiteListed(path)) {
            return chain.filter(exchange);
        }
        // 放行 WebSocket 连接：使用一次性 ticket 鉴权，不校验 token
        if ("/dm/socket".equals(path)) {
            return chain.filter(exchange);
        }

        // 2. 匿名可访问的文章读接口（列表/详情/阅读/用户文章）：不带 token 才放行
        boolean articleAnonymous = path.contains("/article") && !path.contains("/like");
        if (articleAnonymous && (token == null || token.isEmpty())) {
            log.debug("匿名访问文章路径: {}", path);
            return chain.filter(exchange);
        }

        // 3. 匿名可访问的评论读接口
        if ((token == null || token.isEmpty())
                && (path.contains("/comments/list") || path.contains("/comments/replyList"))) {
            log.info("无token，请求路径: {}", path);
            return chain.filter(exchange);
        }

        // 3.5 匿名可访问的公开用户资料接口（/me/profile/{userNo}；/me/profile 精确路径为当前用户，仍需登录）
        if ((token == null || token.isEmpty()) && path.contains("/me/profile/")) {
            log.info("匿名访问用户资料: {}", path);
            return chain.filter(exchange);
        }

        // 4. 其余接口必须带 token
        if (token == null || token.isEmpty()) {
            log.warn("Token缺失或格式错误，请求路径: {}", path);
            return unauthorizedResponse(exchange, "Missing or Invalid Token");
        }

        // 5. 校验 token 并注入用户上下文（文章接口带 token 时也走这里）
        try {
            String userId = JwtUtils.verifyToken(token);
            String role = JwtUtils.getRole(token);
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Role", role)
                    .build();
            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(mutatedRequest)
                    .build();

            log.info("JWT验证通过，用户ID: {}, 角色: {}, 路径: {}", userId, role, path);
            return chain.filter(mutatedExchange);
        } catch (Exception e) {
            log.error("JWT解析错误: {}, 路径: {}", e.getMessage(), path);
            return unauthorizedResponse(exchange, "Invalid Token");
        }
    }

    // 返回401未授权响应
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String msg) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        // ... 设置响应体为JSON格式的错误信息
        return response.setComplete();
    }

    private boolean isWhiteListed(String path) {
        return WHITE_LIST.stream().anyMatch(path::endsWith);
    }

    @Override
    public int getOrder() {
        // 设置为最高优先级，确保在其他过滤器之前执行
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
