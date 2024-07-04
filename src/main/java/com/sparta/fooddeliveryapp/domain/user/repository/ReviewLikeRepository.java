package com.sparta.fooddeliveryapp.domain.user.repository;

import com.sparta.fooddeliveryapp.domain.user.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    int countByUser_UserId(Long userId);
}