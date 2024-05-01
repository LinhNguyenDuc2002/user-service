package com.example.userservice.service.impl;

import com.example.userservice.config.JwtConfig;
import com.example.userservice.constant.ResponseMessage;
import com.example.userservice.dto.request.Credentials;
import com.example.userservice.dto.request.PasswordRequest;
import com.example.userservice.dto.response.AuthResponse;
import com.example.userservice.entity.User;
import com.example.userservice.exception.NotFoundException;
import com.example.userservice.exception.ValidationException;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.token.TokenGenerator;
import com.example.userservice.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenGenerator tokenGenerator;

    public AuthResponse authenticate(Credentials credentials) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(credentials.getUsername().toLowerCase(), credentials.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
//        String token = jwtConfig.generateJwtToken(authentication);
        Map<String, Object> token = tokenGenerator.createToken(authentication);

        log.info("Authenticating credential");
        return AuthResponse.builder()
                .message("Authenticate successfully!")
                .accessToken(token.get("access_token").toString())
                .refreshToken(token.get("refresh_token").toString())
                .build();
    }

    @Override
    public void changePwd(String id, PasswordRequest passwordRequest) throws NotFoundException, ValidationException {
        log.info("Change password of user {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User {} don't exist", id);
                    return NotFoundException.builder()
                            .message(ResponseMessage.USER_NOT_FOUND.getMessage())
                            .build();
                });

        if (!passwordEncoder.matches(passwordRequest.getOldPassword(), user.getPassword())) {
            log.error("Old password and new password don't match");
            throw new ValidationException(passwordRequest, ResponseMessage.OLD_PASSWORD_INVALID.getMessage());
        }

        if (passwordRequest.getNewPassword().equals(passwordRequest.getOldPassword())) {
            log.error("Old password and new password are the same");
            throw new ValidationException(passwordRequest, ResponseMessage.INPUT_PASSWORD_INVALID.getMessage());
        }

        user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));

        log.info("Changed password of user {} successfully", id);
        userRepository.save(user);
    }

    @Override
    public void resetPwd(String id, String password) throws NotFoundException, ValidationException {
        log.info("Reset password of user {}", id);

        if (password.length() < 6 || password.isEmpty()) {
            log.error("New password is invalid");
            throw new ValidationException(password, ResponseMessage.INPUT_PASSWORD_INVALID.getMessage());
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User {} don't exist", id);
                    return NotFoundException.builder()
                            .message(ResponseMessage.USER_NOT_FOUND.getMessage())
                            .build();
                });

        user.setPassword(passwordEncoder.encode(password));

        log.info("Reset password of user {} successfully", id);
        userRepository.save(user);
    }
}
