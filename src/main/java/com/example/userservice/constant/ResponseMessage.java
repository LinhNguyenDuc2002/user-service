package com.example.userservice.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseMessage {
    INPUT_INVALID("The request is invalid"),

    CREATE_USER_SUCCESS("Created a user successfully"),

    DELETE_USER_SUCCESS("Deleted a user successfully"),

    USER_NOT_FOUND("User doesn't exist"),

    GET_ALL_USERS_SUCCESS("Got all users successfully"),

    GET_USER_SUCCESS("Got a user successfully"),

    CHANGE_PASSWORD_SUCCESS("Changed password successfully"),

    RESET_PASSWORD_SUCCESS("Reset password successfully"),

    INPUT_PASSWORD_INVALID("Input password is invalid"),

    OLD_PASSWORD_INVALID("Old password is invalid"),

    USERNAME_EXISTED("Username existed"),

    ADD_CATEGORY_SUCCESS("Added a category successfully"),

    EMAIL_EXISTS("Email already exists"),

    EMAIL_INVALID("Email is invalid"),

    WAIT_ENTER_OTP("Waiting for enter otp"),

    GET_ALL_CATEGORY_SUCCESS("Got all categories successfully"),

    ADD_PRODUCT_SUCCESS("Added a product successfully"),

    UPDATE_PRODUCT_SUCCESS("Updated a product successfully"),

    INVALID_OTP("This is invalid otp"),

    GET_ALL_PRODUCT_SUCCESS("Got all products successfully"),

    GET_PRODUCT_SUCCESS("Got the product successfully"),

    DELETE_PRODUCT_SUCCESS("Deleted a product successfully"),

    GET_COMMENTS_SUCCESS("Got comments successfully"),

    CREATE_COMMENT_SUCCESS("Create a comment successfully"),

    UPDATE_COMMENT_SUCCESS("Update a comment successfully"),

    UPDATE_USER_SUCCESS("Update a user successfully"),

    DELETE_COMMENT_SUCCESS("Deleted a comment successfully"),

    PRODUCT_NOT_FOUND("Product doesn't exist"),

    COMMENT_NOT_FOUND("Comment doesn't exist"),

    CATEGORY_NOT_FOUND("Category doesn't exist"),

    GET_ADDRESS_SUCCESS("Get address successfully"),

    UPDATE_ADDRESS_SUCCESS("Update address successfully"),

    ITEM_NOT_FOUND("Item doesn't exist"),

    DELETE_ITEM_SUCCESS("Deleted a item successfully"),

    ADD_ITEM_SUCCESS("Added a item successfully"),

    UPDATE_ITEM_SUCCESS("Updated a item successfully"),

    GET_ALL_BILLS_SUCCESS("Got all bills successfully"),

    GET_BILL_SUCCESS("Got the bill successfully"),

    BILL_NOT_FOUND("Bill doesn't exist"),

    CREATE_BILL_SUCCESS("Created a bill successfully"),

    PAID_SUCCESS("Paid successfully"),

    ERROR_USER_UNKNOWN(""),

    SET_AVATAR_SUCCESS(""),

    DELETE_AVATAR_SUCCESS("");

    private String message;
}
