package com.gather_club_back.gather_club_back.service.impl;

import com.gather_club_back.gather_club_back.entity.User;
import com.gather_club_back.gather_club_back.mapper.UserMapper;
import com.gather_club_back.gather_club_back.model.UserResponse;
import com.gather_club_back.gather_club_back.repository.UserRepository;
import com.gather_club_back.gather_club_back.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse getUser(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
        return userMapper.toUserResponse(user);
    }
}
