package org.ggyool.apigatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {

    public CustomFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // Custom Pre Filter
        return (exchange, chain) -> {
            // reactive 환경에서는 HttpServletRequest 가 아닌 ServerHttpRequest
            ServerHttpRequest serverHttpRequest = exchange.getRequest();
            ServerHttpResponse serverHttpResponse = exchange.getResponse();

            log.info("Custom Pre filter: request id -> {}", serverHttpRequest.getId());

            // Custom Post Filter
            return chain.filter(exchange)
                    .then(
                            Mono.fromRunnable(() -> {
                                log.info("Custom Post filter: request id -> {}", serverHttpResponse.getStatusCode());
                            })
                    );
        };
    }

    public static class Config {

    }
}
