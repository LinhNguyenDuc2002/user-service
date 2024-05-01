package com.example.userservice.security.resourceserver;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TokenResolver implements BearerTokenResolver {
    @Override
    public String resolve(HttpServletRequest request) {
        return null;
    }
}
