package com.learnit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learnit.models.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>{

}
