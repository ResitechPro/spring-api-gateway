package com.resitechpro.web.feign;

import com.resitechpro.domain.dto.request.ValidateTokenRequestDto;
import com.resitechpro.utils.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "AUTHENTIFICATION")
public interface AuthClient {
    final String URL_PREFIX = "/api/v1";
    @PostMapping(URL_PREFIX + "/auth/validate-token")
    Response<Boolean> validateToken(
            @RequestHeader("X-tenant-id") String tenantId,
            ValidateTokenRequestDto validateTokenRequestDto
    );

}
