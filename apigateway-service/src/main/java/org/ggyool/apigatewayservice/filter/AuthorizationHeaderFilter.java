package org.ggyool.apigatewayservice.filter;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private final Environment environment;

    public AuthorizationHeaderFilter(Environment environment) {
        super(Config.class);
        this.environment = environment;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwtToken = authorizationHeader.replace("Bearer ", "");

            if (!isJwtValid(jwtToken)) {
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }
            // TODO
            // User 한 명의 정보를 얻는 API 가 필요하다고 가정하자.
            // 클라이언트에서 userId를 토큰에 포함하고 GET /users/{userId} 요청을 보내야
            //            // REST 한지는 모르곘지만 gateway 에서는 GET /users/me하는 것인가?
            // 토큰과 uri 에 2중으로 userId가 담겨있는 느낌이 든다. 요청을 받고
            // UserService 에는 /user/{userId} 를 요청한다면 어떨까?
            // 어떻게 forwarding 할 수 있을지 고민해 보자.
            return chain.filter(exchange);
        };
    }



    // Webflux 에서 단일값이면 Mono, 아니면 Flux
    private Mono<Void> onError(ServerWebExchange exchange, String errorMessage, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(errorMessage);
        return response.setComplete();
    }

    private boolean isJwtValid(String jwtToken) {
        String subject;
        try {
            subject = Jwts.parser()
                    .setSigningKey(environment.getProperty("token.secret"))
                    .parseClaimsJws(jwtToken)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            return false;
        }

        return subject != null && !subject.isEmpty();
    }

    public static class Config {

    }
}
