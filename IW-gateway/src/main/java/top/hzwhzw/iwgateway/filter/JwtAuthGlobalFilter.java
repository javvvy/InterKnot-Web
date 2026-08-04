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

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // 1. 放行登录等公开路径
        if (path.contains("/auth/login") || path.contains("/public")) {
            log.debug("放行公开路径: {}", path);
            return chain.filter(exchange);
        }
        // 2. 获取并校验Token
        String authHeader = request.getHeaders().getFirst("token");

        if (authHeader == null || authHeader.isEmpty()) {

            // 可含可不含Token的路径
            if (path.contains("/comments/list") || path.contains("/comments/replyList")){
                //  无token
                log.info("无token，请求路径: {}", path);
                return chain.filter(exchange);
            }else {
                log.warn("Token缺失或格式错误，请求路径: {}", path);
                return unauthorizedResponse(exchange, "Missing or Invalid Token");
            }
        }
        String token = authHeader;
        try {
            // 3. 解析JWT
            String userId = JwtUtils.verifyToken(token);
            String role = JwtUtils.getRole(token);
            // 4. 将用户信息传递给下游服务
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-Role", role)
                    .build();
            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(mutatedRequest)
                    .build();

            // 5. 放行请求
            log.info("JWT验证通过，用户ID: {}, 角色: {}, 路径: {}", userId, role, path);
            return chain.filter(mutatedExchange);
        } catch (Exception e) {
            // Token解析失败
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

    @Override
    public int getOrder() {
        // 设置为最高优先级，确保在其他过滤器之前执行
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
