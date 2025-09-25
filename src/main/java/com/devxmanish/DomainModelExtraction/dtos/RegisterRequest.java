package com.devxmanish.DomainModelExtraction.dtos;

import jakarta.validation.constraints.NotNull;

public record RegisterRequest(

        @NotNull(message = "Name is required")
        String name,

        @NotNull(message = "Username is required")
        String username,

        @NotNull(message = "Password is required")
        String password
) {
}
