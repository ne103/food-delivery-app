package com.sparta.fooddeliveryapp.domain.follow.service;

import com.sparta.fooddeliveryapp.domain.follow.dto.FollowRequestDto;
import com.sparta.fooddeliveryapp.domain.follow.dto.FollowResponseDto;
import com.sparta.fooddeliveryapp.domain.follow.entity.Follow;
import com.sparta.fooddeliveryapp.domain.follow.repository.FollowRepository;
import com.sparta.fooddeliveryapp.domain.like.entity.UserLike;
import com.sparta.fooddeliveryapp.domain.store.entity.Store;
import com.sparta.fooddeliveryapp.domain.store.repository.StoreRepository;
import com.sparta.fooddeliveryapp.domain.user.entity.User;
import com.sparta.fooddeliveryapp.global.error.exception.DuplicateLikeException;
import jakarta.validation.constraints.Null;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final StoreRepository storeRepository;

    public void addUserLike(User user, FollowRequestDto followRequestDto) {
        Store store = storeRepository.findById(followRequestDto.getStoreId()).orElseThrow(
                () -> new NullPointerException("없는 매장입니다")
        );

        if(followRepository.existsByUserAndStore(user, store)){
            throw new DuplicateLikeException("이미 팔로우 중 입니다");
        }
        followRepository.save(
                Follow.builder()
                        .store(store)
                        .user(user)
                        .build());
    }

    public void deleteUserLike(User user, FollowRequestDto followRequestDto) {
        Store store = storeRepository.findById(followRequestDto.getStoreId()).orElseThrow(
                () -> new NullPointerException("없는 매장입니다")
        );
        Follow follow = followRepository.findByUserAndStore(user, store).orElseThrow(
                () -> new NullPointerException("팔로우 상태가 아닙니다")
        );
        followRepository.delete(follow);
    }

    public List<FollowResponseDto> getFollowList(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Follow> followPage = followRepository.findAllByUserOrderByCreatedAtDesc(user, pageable);
        return followPage.stream().map(
                follow -> FollowResponseDto.builder()
                        .followId(follow.getFollowerId())
                        .storeId(follow.getStore().getStoreId())
                        .userId(follow.getUser().getUserId())
                        .build()
        ).toList();
    }
}
