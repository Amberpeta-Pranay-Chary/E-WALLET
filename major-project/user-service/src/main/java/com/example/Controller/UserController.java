package com.example.Controller;

import com.example.Model.User;
import com.example.Service.UserService;
import com.example.Utils;
import com.example.dto.CreateUserRequest;
import com.example.dto.GetUserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/user/{userId}") //Creating an API in the System.
    public GetUserResponse createUser(@PathVariable("userId") Integer userId) throws Exception {
       //Assignment Need to complete that Same API with different purpse like in library management system with book name.
        User user=userService.get(userId);
        return Utils.convertToUserResponse(user);
    }
    @GetMapping("/user/phone/{phone}") //Creating an API in the System.
    public GetUserResponse createUser(@PathVariable("phone") String phoneNumber) throws Exception {
        //Assignment Need to complete that Same API with different purpse like in library management system with book name.
        User user=userService.getByPhone(phoneNumber);
        return Utils.convertToUserResponse(user);
    }

    @PostMapping("/user") //Creating an API in the System.
    public void createUser(@RequestBody @Valid CreateUserRequest createUserRequest) throws Exception
    {
        userService.create(Utils.convertUserCreateRequest(createUserRequest));

    }
}
