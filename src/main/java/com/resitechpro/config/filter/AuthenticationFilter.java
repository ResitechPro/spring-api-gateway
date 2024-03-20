package com.resitechpro.config.filter;

import com.resitechpro.domain.dto.request.ValidateTokenRequestDto;
import com.resitechpro.utils.Response;
import com.resitechpro.utils.RouteValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouteValidator validator;
    private final WebClient.Builder webClientBuilder;
    private final DiscoveryClient discoveryClient;

    public AuthenticationFilter(
        RouteValidator validator,
        WebClient.Builder webClientBuilder,
        DiscoveryClient discoveryClient
    ) {
        super(Config.class);
        this.validator = validator;
        this.webClientBuilder = webClientBuilder;
        this.discoveryClient = discoveryClient;
    }
    @Override
    public GatewayFilter apply(Config config){
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (validator.isSecured.test(request)) {
                String headerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (headerToken == null || !headerToken.startsWith("Bearer ")) {
                    throw new RuntimeException("Unauthorized, missing token");
                }else{
                    headerToken = headerToken.substring(7);
                    List<ServiceInstance> instances = discoveryClient.getInstances("AUTHENTIFICATION");
                    if(instances.isEmpty()){
                        throw new RuntimeException("Something went wrong");
                    }else{
                        String authBaseUrl = instances.get(0).getUri().toString();
                        return webClientBuilder.build()
                            .post()
                            .uri(authBaseUrl + "/api/v1/auth/validate-token")
                            .header("X-tenant-id", request.getHeaders().getFirst("X-tenant-id"))
                            .bodyValue(
                                    ValidateTokenRequestDto.builder()
                                            .accessToken(headerToken)
                                            .build()
                            )
                            .retrieve()
                            .onStatus(t -> {
                                return t.equals(HttpStatus.UNAUTHORIZED) || t.equals(HttpStatus.FORBIDDEN) || t.equals(HttpStatus.INTERNAL_SERVER_ERROR);
                            }, response -> Mono.error(new RuntimeException("Something went wrong")))
                            .bodyToMono(Response.class).flatMap(response -> {
                                if (Boolean.FALSE.equals( (boolean) response.getResult())) {
                                    return Mono.error(new RuntimeException("Invalid token"));
                                }
                                return chain.filter(exchange);
                            });
                    }
                }
            }
            return chain.filter(exchange);
        };
    }
    public static class Config {
    }
}
