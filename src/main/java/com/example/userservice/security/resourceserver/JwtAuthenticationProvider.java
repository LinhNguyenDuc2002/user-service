package com.example.userservice.security.resourceserver;

import com.example.userservice.constant.OAuthError;
import com.example.userservice.constant.SecurityConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class is used to read the access token from request header
 */
@Component
@Slf4j
public class JwtAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private JwtDecoder jwtDecoder;

    /**
     * Authenticate info provided in authentication
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        BearerTokenAuthenticationToken token = (BearerTokenAuthenticationToken) authentication;

        Jwt jwt;
        try {
            jwt = this.jwtDecoder.decode(token.getToken()); //decode token
        }
        catch (Exception e) {
            log.error("Invalid token: {}", e.getMessage());
            return null;
        }

        //check Bearer token
        if (!OAuth2AccessToken.TokenType.BEARER.getValue().equals(jwt.getClaimAsString(SecurityConstant.TOKEN_CLAIM_TYPE))) {
            log.error("Invalid token type");
            throw new OAuth2AuthenticationException(OAuthError.INVALID_REQUEST);
        }

        // verify if session is still valid
//        String sessionId = jwt.getClaimAsString(SecurityConstant.TOKEN_CLAIM_SESSION_ID);
//        Optional<UserSession> userSessionOpt = userSessionRepository.findById(sessionId);
//        if (userSessionOpt.isEmpty()) {
//            log.error("User session is unknown or expired");
//            throw new OAuth2AuthenticationException(OAuthError.INVALID_REQUEST);
//        }
//
//        List<GrantedAuthority> authorities = new ArrayList<>();
//        if (jwt.hasClaim(SecurityConstant.TOKEN_CLAIM_PERMISSION)) {
//            List<Long> permissionClaims = jwt.getClaim(SecurityConstant.TOKEN_CLAIM_PERMISSION);
//            for (Long p : permissionClaims) {
//                authorities.add(new SimpleGrantedAuthority(String.valueOf(p)));
//            }
//        }
//
//        return new JwtAuthenticationToken(jwt, authorities);

        return new JwtAuthenticationToken(jwt);
    }

    /**
     * Does JwtAuthenticationProvider support for authenticating any specific authentication class?
     * @param authentication
     * @return
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(BearerTokenAuthenticationToken.class);
    }
}
