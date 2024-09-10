package com.project.e_wallet.fiilter;

import com.project.e_wallet.Config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CustomGatewayFilter implements GlobalFilter {

    @Autowired
    AppConfig restTemplate;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        String requestPath = exchange.getRequest().getPath().toString();
        System.out.println(requestPath);
        if (HttpMethod.POST.equals(exchange.getRequest().getMethod())&&(requestPath.startsWith("/login")||requestPath.startsWith("/user"))) {
            // Skip the filter and continue the chain
            return chain.filter(exchange);
        }
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String jwtToken = authorizationHeader.substring(7);
        // Add logic to validate the token
        Boolean check=restTemplate.getTemplate().getForObject("http://localhost:9000/validate?token="+jwtToken,Boolean.class);
        if(check==false)
        {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        // Continue to the next filter if valid
        return chain.filter(exchange);
    }
}
