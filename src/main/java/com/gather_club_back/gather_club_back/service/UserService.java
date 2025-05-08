package com.gather_club_back.gather_club_back.service;

import com.gather_club_back.gather_club_back.model.UserResponse;

public interface UserService {
    UserResponse getUser(Integer userId);
}
