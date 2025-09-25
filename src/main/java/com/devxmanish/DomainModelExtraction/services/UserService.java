package com.devxmanish.DomainModelExtraction.services;

import com.devxmanish.DomainModelExtraction.dtos.LoginRequest;
import com.devxmanish.DomainModelExtraction.dtos.RegisterRequest;
import com.devxmanish.DomainModelExtraction.dtos.Response;
import com.devxmanish.DomainModelExtraction.models.User;

public interface UserService {
    Response<?> createUser(RegisterRequest request);
    Response<?> login(LoginRequest request);
    User getCurrentLoggedInUser();
}
