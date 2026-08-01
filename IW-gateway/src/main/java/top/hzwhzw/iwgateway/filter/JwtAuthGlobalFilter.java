package top.hzwhzw.iwgateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import utils.JwtUtils;

@Component
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // 1. 放行登录等公开路径
        if (path.contains("/auth/login") || path.contains("/public")) {
            return chain.filter(exchange);
        }

        // 2. 获取并校验Token
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorizedResponse(exchange, "Missing or Invalid Token");
        }
        String token = authHeader.substring(7);

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
            return chain.filter(mutatedExchange);
        } catch (Exception e) {
            // Token解析失败
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
        // 设置过滤器执行顺序，值越小越先执行
        // 需确保在 NettyRoutingFilter 之前执行
        return -1;
    }
}
