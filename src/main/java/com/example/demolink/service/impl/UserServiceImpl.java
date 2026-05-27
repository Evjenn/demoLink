package com.example.demolink.service.impl;

import com.example.demolink.exception.BaseException;
import com.example.demolink.model.dto.request.RegisterRequest;
import com.example.demolink.model.entity.UserEntity;
import com.example.demolink.repository.UserRepository;
import com.example.demolink.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BaseException("Username '"
                    + request.getUsername() + "' is already taken", HttpStatus.CONFLICT);
        }

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

    }
}
