package com.devxmanish.DomainModelExtraction.repos;

import com.devxmanish.DomainModelExtraction.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long > {
    Optional<User> findByUsername(String username);
}
