//package com.example.userservice.service.impl;
//
//import com.example.userservice.config.RabbitMQConfig;
//import com.example.userservice.constant.RoleType;
//import com.example.userservice.entity.Role;
//import com.example.userservice.entity.User;
//import com.example.userservice.exception.ValidationException;
//import com.example.userservice.payload.CustomerRequest;
//import com.example.userservice.service.ProductService;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class ProductServiceImpl implements ProductService {
//    @Autowired
//    private RabbitTemplate template;
//
//    @Override
//    public void createActor(User user) throws ValidationException {
//        List<RoleType> roles = user.getRoles().stream().map(Role::getRoleName).toList();
//
//        if (!roles.contains(RoleType.CUSTOMER) && !roles.contains(RoleType.EMPLOYEE)) {
//            throw ValidationException.builder().message("abc").build();
//        }
//
//        CustomerRequest customerRequest = CustomerRequest.builder()
//                .accountId(user.getId())
//                .fullname(user.getFullname())
//                .email(user.getEmail())
//                .phone(user.getPhone())
//                .role(roles.get(0).name())
//                .build();
//
//        send(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, customerRequest);
//    }
//
//    private void send(String exchange, String routingKey, Object object) {
//        template.convertAndSend(exchange, routingKey, object);
//    }
//}
