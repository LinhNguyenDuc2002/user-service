package com.example.userservice.util;

import com.example.userservice.security.data.AuthUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {
    /**
     * @return
     */
    public static String getLoggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }

        AuthUser userPrincipal = (AuthUser) authentication.getPrincipal();
        return userPrincipal.getId().toString();
    }
}
