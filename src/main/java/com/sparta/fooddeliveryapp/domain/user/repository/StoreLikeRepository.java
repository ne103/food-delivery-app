package com.sparta.fooddeliveryapp.domain.user.repository;

import com.sparta.fooddeliveryapp.domain.user.entity.StoreLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreLikeRepository extends JpaRepository<StoreLike, Long> {
    int countByUser_UserId(Long userId);
}