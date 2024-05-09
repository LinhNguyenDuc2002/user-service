package com.example.userservice.controller;

import com.example.userservice.constant.ResponseMessage;
import com.example.userservice.dto.request.Credentials;
import com.example.userservice.dto.request.PasswordRequest;
import com.example.userservice.dto.response.AuthResponse;
import com.example.userservice.dto.response.CommonResponse;
import com.example.userservice.exception.NotFoundException;
import com.example.userservice.exception.ValidationException;
import com.example.userservice.service.AuthService;
import com.example.userservice.util.HandleBindingResult;
import com.example.userservice.util.ResponseUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody Credentials credentials, BindingResult bindingResult) throws ValidationException {
        HandleBindingResult.handle(bindingResult, credentials);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/change-pwd")
    public ResponseEntity<CommonResponse<Void>> changePassword(@PathVariable String id,
                                                               @Valid @RequestBody PasswordRequest pwd,
                                                               BindingResult bindingResult) throws ValidationException, NotFoundException {
        HandleBindingResult.handle(bindingResult, pwd);
        authService.changePwd(id, pwd);
        return ResponseUtil.wrapResponse(null, ResponseMessage.CHANGE_PASSWORD_SUCCESS.getMessage());
    }

    @PutMapping("/{id}/reset-pwd")
    public ResponseEntity<CommonResponse<Void>> resetPassword(@RequestParam(name = "password") String password,
                                                              @PathVariable String id) throws ValidationException, NotFoundException {
        authService.resetPwd(id, password);
        return ResponseUtil.wrapResponse(null, ResponseMessage.RESET_PASSWORD_SUCCESS.getMessage());
    }
}
