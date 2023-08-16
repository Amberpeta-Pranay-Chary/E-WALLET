package com.example;

import com.example.Model.User;
import com.example.dto.CreateUserRequest;
import com.example.dto.GetUserResponse;

public class Utils {
        public static User convertUserCreateRequest(CreateUserRequest request)
        {
            return User.builder().name(request.getName()).phone_number(request.getPhone_number()).age(request.getAge())
                    .email(request.getEmail()).build();
        }
        public static GetUserResponse convertToUserResponse(User user)
        {
            return GetUserResponse.builder().name(user.getName()).age(user.getAge()).email(user.getEmail())
                    .createdOn(user.getCreatedOn()).updatedOn(user.getUpdatedOn()).build();
        }
}
