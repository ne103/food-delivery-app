package com.sparta.fooddeliveryapp.domain.follow.repository;

import com.sparta.fooddeliveryapp.domain.follow.entity.Follow;
import com.sparta.fooddeliveryapp.domain.store.entity.Store;
import com.sparta.fooddeliveryapp.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByUserAndStore(User user, Store store);

    Optional<Follow> findByUserAndStore(User user, Store store);

    Page<Follow> findAllByUserOrderByCreatedAtDesc(User user, Pageable pageable);

}
