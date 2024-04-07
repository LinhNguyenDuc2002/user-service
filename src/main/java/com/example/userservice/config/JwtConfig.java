package com.example.userservice.config;

import com.example.userservice.entity.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
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

    /**
     * Create token from credentials
     *
     * @param authentication
     * @return
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        Date now = new Date();

        String token = Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("id", userPrincipal.getId())
                .claim("authority", userPrincipal.getAuthorities())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expireTime))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();

        return token;
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
            return true;
        } catch (UnsupportedJwtException exp) {
            System.out.println("claimsJws argument does not represent Claims JWS" + exp.getMessage());
        } catch (MalformedJwtException exp) {
            System.out.println("claimsJws string is not a valid JWS" + exp.getMessage());
        } catch (SignatureException exp) {
            System.out.println("claimsJws JWS signature validation failed" + exp.getMessage());
        } catch (ExpiredJwtException exp) {
            System.out.println("Claims has an expiration time before the method is invoked" + exp.getMessage());
        } catch (IllegalArgumentException exp) {
            System.out.println("claimsJws string is null or empty or only whitespace" + exp.getMessage());
        }
        return false;
    }

    /**
     * Get user information from token
     *
     * @param token
     * @return
     */
    public String getUserNameFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Create token from credentials
     *
     * @param authentication
     * @return
     */
    public String generateRefreshToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        Date now = new Date();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenExpireTime))
                .signWith(SignatureAlgorithm.HS512, refreshTokenSecretKey)
                .compact();
    }
}
