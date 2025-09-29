package com.devxmanish.DomainModelExtraction.services.impl;

import com.devxmanish.DomainModelExtraction.dtos.LoginRequest;
import com.devxmanish.DomainModelExtraction.dtos.RegisterRequest;
import com.devxmanish.DomainModelExtraction.dtos.Response;
import com.devxmanish.DomainModelExtraction.enums.Role;
import com.devxmanish.DomainModelExtraction.exceptions.BadRequestException;
import com.devxmanish.DomainModelExtraction.exceptions.NotFoundException;
import com.devxmanish.DomainModelExtraction.models.User;
import com.devxmanish.DomainModelExtraction.repos.UserRepository;
import com.devxmanish.DomainModelExtraction.security.JwtUtils;
import com.devxmanish.DomainModelExtraction.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Response<?> createUser(RegisterRequest request) {
        Optional<User> existingUser = userRepository.findByUsername(request.username());

        if(existingUser.isPresent()){
            throw new BadRequestException("User already exists");
        }

        User user = User.builder()
                .name(request.name())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("User registered successfully")
                .build();
    }

    @Override
    public Response<?> login(LoginRequest request) {
        log.info("Inside login()");

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(()-> new NotFoundException("User not registered yet"));

        if(!passwordEncoder.matches(request.password(), user.getPassword())){
            throw new BadRequestException("Invalid password");
        }

        String token = jwtUtils.generateToken(request.username());

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Login successfull")
                .data(token)
                .build();
    }

    @Override
    public User getCurrentLoggedInUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(()-> new NotFoundException("User not found"));
    }
}
