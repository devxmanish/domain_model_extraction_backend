package com.devxmanish.DomainModelExtraction.components;

import com.devxmanish.DomainModelExtraction.security.AuthUser;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAware")
public class CustomAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // Example using Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Optional.of("SYSTEM");
        }

        Object principle = auth.getPrincipal();
        if (principle instanceof AuthUser authUser){
            return Optional.of(authUser.getUsername());
        }

        // fallback to username if not CustomUserDetails
        return Optional.of(auth.getName());
    }
}
