package com.example.userservice.config;

import com.example.userservice.security.data.AuthUser;
import com.example.userservice.security.util.KeyUtil;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtConfig {
    @Value("${jwtToken.secretKey}")
    private String secretKey;

    @Value("${jwtToken.expireTime}")
    private long expireTime;

    @Value("${jwtRefreshToken.secretKey}")
    private String refreshTokenSecretKey;

    @Value("${jwtRefreshToken.expireTime}")
    private long refreshTokenExpireTime;

    @Autowired
    private KeyUtil keyUtil;

    @Bean
    @Qualifier("jwtAccessTokenEncoder")
    public JwtEncoder jwtAccessTokenEncoder() {
        JWK jwk = new RSAKey
                .Builder(keyUtil.getAccessTokenPublicKey())
                .privateKey(keyUtil.getAccessTokenPrivateKey())
                .build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk)); //Don't allow to change JWKSet after created
        return new NimbusJwtEncoder(jwks); //encode and create JWT token
    }

    @Bean
    @Qualifier("jwtRefreshTokenEncoder")
    JwtEncoder jwtRefreshTokenEncoder() {
        JWK jwk = new RSAKey
                .Builder(keyUtil.getRefreshTokenPublicKey())
                .privateKey(keyUtil.getRefreshTokenPrivateKey())
                .build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

//    /**
//     * Create token from credentials
//     *
//     * @param authentication
//     * @return
//     */
//    public String generateJwtToken(Authentication authentication) {
//        AuthUser userPrincipal = (AuthUser) authentication.getPrincipal();
//        Date now = new Date();
//
//        String token = Jwts.builder()
//                .setSubject(userPrincipal.getUsername())
//                .claim("id", userPrincipal.getId())
//                .claim("authority", userPrincipal.getAuthorities())
//                .setIssuedAt(now)
//                .setExpiration(new Date(now.getTime() + expireTime))
//                .signWith(SignatureAlgorithm.HS512, secretKey)
//                .compact();
//
//        return token;
//    }
//
//    public boolean validateJwtToken(String token) {
//        try {
//            Jwts.parser()
//                    .setSigningKey(secretKey)
//                    .parseClaimsJws(token);
//            return true;
//        } catch (UnsupportedJwtException exp) {
//            System.out.println("claimsJws argument does not represent Claims JWS" + exp.getMessage());
//        } catch (MalformedJwtException exp) {
//            System.out.println("claimsJws string is not a valid JWS" + exp.getMessage());
//        } catch (SignatureException exp) {
//            System.out.println("claimsJws JWS signature validation failed" + exp.getMessage());
//        } catch (ExpiredJwtException exp) {
//            System.out.println("Claims has an expiration time before the method is invoked" + exp.getMessage());
//        } catch (IllegalArgumentException exp) {
//            System.out.println("claimsJws string is null or empty or only whitespace" + exp.getMessage());
//        }
//        return false;
//    }
//
//    /**
//     * Get user information from token
//     *
//     * @param token
//     * @return
//     */
//    public String getUserNameFromJwtToken(String token) {
//        Claims claims = Jwts.parser()
//                .setSigningKey(secretKey)
//                .parseClaimsJws(token)
//                .getBody();
//
//        return claims.getSubject();
//    }
}
