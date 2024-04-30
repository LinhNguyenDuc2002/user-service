package com.example.userservice.service.impl;

import com.example.userservice.cache.UserCacheManager;
import com.example.userservice.config.ApplicationConfig;
import com.example.userservice.constant.ResponseMessage;
import com.example.userservice.constant.RoleType;
import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.request.UserRequest;
import com.example.userservice.entity.Role;
import com.example.userservice.entity.User;
import com.example.userservice.exception.NotFoundException;
import com.example.userservice.exception.ValidationException;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.message.EmailService;
import com.example.userservice.message.email.EmailConstant;
import com.example.userservice.message.email.EmailMessage;
import com.example.userservice.redis.model.UserCache;
import com.example.userservice.repository.RoleRepository;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.ProductService;
import com.example.userservice.service.UserService;
import com.example.userservice.util.OtpUtil;
import com.example.userservice.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserCacheManager userCacheManager;

    @Autowired
    private ProductService iamService;

    @Autowired
    private ApplicationConfig applicationConfig;

    @Override
    public UserDto getLoggedInUser() throws NotFoundException {
        log.info("Get info of logged in user");
        String id = SecurityUtil.getLoggedInUserId();

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Logged in user don't exist");
                    return NotFoundException.builder()
                            .message(ResponseMessage.USER_NOT_FOUND.getMessage())
                            .build();
                });

        log.info("Got info of logged in user successfully");
        return userMapper.toDto(user);
    }

    @Override
    public void createTempUser(UserRequest newUserRequest) throws ValidationException {
        log.info("Save registration information temporarily");

        if (!StringUtils.hasText(newUserRequest.getEmail())) {
            log.error("Email is invalid");
            throw new ValidationException(newUserRequest, ResponseMessage.EMAIL_INVALID.getMessage());
        }
        if (userRepository.existsByEmail(newUserRequest.getEmail())) {
            log.error("Email is already in use");
            throw new ValidationException(newUserRequest, ResponseMessage.EMAIL_EXISTS.getMessage());
        }

        UserCache userCache = convertToUserCache(newUserRequest);

        String otp = OtpUtil.generateOTP();
        userCache.setOtp(otp);
        userCache.setSecretKey(UUID.randomUUID().toString());

        Map<String, String> emailArgs = new HashMap<>();
        emailArgs.put(EmailConstant.ARG_LOGO_URI, "");
        emailArgs.put(EmailConstant.ARG_OTP_CODE, otp);
        emailArgs.put(EmailConstant.ARG_RECEIVER_NAME, userCache.getFullname());
        emailArgs.put(EmailConstant.ARG_SUPPORT_EMAIL, applicationConfig.getSenderEmail());

        EmailMessage email = EmailMessage.builder()
                .template(EmailConstant.TEMPLATE_EMAIL_VERIFY_OTP)
                .receiver(userCache.getEmail())
                .sender(applicationConfig.getSenderEmail())
                .subject(EmailConstant.ARG_VERIFY_EMAIL_SUBJECT)
                .args(emailArgs)
                .locale(LocaleContextHolder.getLocale())
                .build();
        log.info("Sending OTP to authenticate ...");
        emailService.sendMessage(email);
        log.info("OTP code is sent successfully");

        userCacheManager.storeUserCache(userCache);
    }

    @Override
    public UserDto createUser(String id, String otp, String secret) throws ValidationException, NotFoundException {
        log.info("Verifying otp ...");

        if (!StringUtils.hasText(otp)) {
            log.error("OTP code is invalid or expired");
            throw ValidationException.builder()
                    .errorObject(otp)
                    .message(ResponseMessage.INVALID_OTP.getMessage())
                    .build();
        }

        UserCache userCache = userCacheManager.verifyUserCache(id, otp, secret);
        if (userCache == null) {
            throw NotFoundException.builder()
                    .message(ResponseMessage.USER_NOT_FOUND.getMessage())
                    .build();
        }

        User user = convertToUser(userCache);
        userCacheManager.clearUserCache(userCache.getId());

        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByRoleName(RoleType.valueOf(userCache.getRole())));

        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        iamService.createActor(user);
        return userMapper.toDto(user);
    }

    @Override
    public void delete(String id) throws NotFoundException {
        log.info("Delete user {}", id);

        boolean checkUser = userRepository.existsById(id);
        if (!checkUser) {
            log.error("User {} don't exist", id);
            throw NotFoundException.builder()
                    .message(ResponseMessage.USER_NOT_FOUND.getMessage())
                    .build();
        }

        userRepository.deleteById(id);
        log.info("Deleted user with {}", id);
    }

    @Override
    public List<UserDto> getAll() {
        log.info("Get all users");
        return userMapper.toDtoList(userRepository.findAll());
    }

    @Override
    public UserDto get(String id) throws NotFoundException {
        log.info("Get user {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User {} don't exist", id);
                    return NotFoundException.builder()
                            .message(ResponseMessage.USER_NOT_FOUND.getMessage())
                            .build();
                });

        log.info("Got user {} successfully", id);
        return userMapper.toDto(user);
    }

    @Override
    public UserDto update(String id, UserRequest userRequest) throws NotFoundException, ValidationException {
        log.info("Update user {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User {} don't exist", id);
                    return NotFoundException.builder()
                            .message(ResponseMessage.USER_NOT_FOUND.getMessage())
                            .build();
                });

        if (!user.getUsername().equals(userRequest.getUsername())) {
            boolean checkUsername = userRepository.existsByUsername(userRequest.getUsername());

            if (checkUsername) {
                log.error("Username {} existed", userRequest.getUsername());
                throw new ValidationException(userRequest, ResponseMessage.USERNAME_EXISTED.getMessage());
            }
            user.setUsername(userRequest.getUsername());
        }

        user.setFullname(userRequest.getFullname());
        user.setDob(userRequest.getDob());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());

        userRepository.save(user);

        log.info("Updated user {} successfully", id);
        return userMapper.toDto(user);
    }

    public User convertToUser(UserCache userCache) {
        return User.builder()
                .username(userCache.getUsername())
                .password(userCache.getPassword())
                .fullname(userCache.getFullname())
                .email(userCache.getEmail())
                .phone(userCache.getPhone())
                .dob(userCache.getDob())
                .build();
    }

    public UserCache convertToUserCache(UserRequest newUserRequest) {
        return UserCache.builder()
                .id(UUID.randomUUID().toString())
                .username(newUserRequest.getUsername())
                .password(newUserRequest.getPassword())
                .fullname(newUserRequest.getFullname())
                .dob(newUserRequest.getDob())
                .phone(newUserRequest.getPhone())
                .email(newUserRequest.getEmail())
                .role(newUserRequest.getRole())
                .build();
    }
}
