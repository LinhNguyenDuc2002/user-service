package com.example.userservice.service.impl;

import com.example.userservice.constant.ResponseMessage;
import com.example.userservice.entity.User;
import com.example.userservice.entity.UserDetailsImpl;
import com.example.userservice.exception.CommonRuntimeException;
import com.example.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    return CommonRuntimeException.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message(ResponseMessage.USER_NOT_FOUND.getMessage())
                            .build();
                });

        return new UserDetailsImpl(user);
    }
}
