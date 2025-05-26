package com.HippoNuage.User.user_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.HippoNuage.User.user_service.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    //Here we already have all CRUD methods ready from JPA repo
    Optional<User> findByEmail(String email);
}
