package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.entity.User;
import com.gather_club_back.gather_club_back.model.UserResponse;
import com.gather_club_back.gather_club_back.service.UserService;
import com.gather_club_back.gather_club_back.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("{userId}")
    public UserResponse getUser(@PathVariable Integer userId){
        return userService.getUser(userId);
    }
    @GetMapping("test")
    public String test() {
        return "OK";
    }
}
