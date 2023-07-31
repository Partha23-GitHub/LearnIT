package com.learnit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learnit.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByEmail(String email);
}
