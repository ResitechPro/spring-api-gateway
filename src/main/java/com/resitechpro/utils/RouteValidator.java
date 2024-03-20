package com.resitechpro.utils;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    private RouteValidator() {
    }

    public static final List<String> openApiEndpoints = List.of(
            "/auth/sign-up",
            "/auth/sign-in",
            "/auth/refresh-token",
            "/auth/validate-token",
            "/eureka"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}
