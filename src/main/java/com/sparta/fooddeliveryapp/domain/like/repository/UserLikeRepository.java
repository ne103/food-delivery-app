package com.sparta.fooddeliveryapp.domain.like.repository;

import com.sparta.fooddeliveryapp.domain.like.entity.UserLike;
import com.sparta.fooddeliveryapp.domain.like.entity.UserLikeType;
import com.sparta.fooddeliveryapp.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLikeRepository extends JpaRepository<UserLike, Long> {

    boolean existsByUserAndUserLikeTypeAndTypeId(User user, UserLikeType userLikeType, Long typeId);

    Optional<UserLike> findByUserAndUserLikeTypeAndTypeId(User user, UserLikeType userLikeType, Long typeId);

    Page<UserLike> findAllByUserLikeTypeAndTypeIdOrderByCreatedAtDesc(UserLikeType userLikeType, Long typeId, Pageable pageable);


    Page<UserLike> findByUserAndUserLikeTypeOrderByCreatedAtDesc(User user, UserLikeType userLikeType, Pageable pageable);
}
