package com.sparta.fooddeliveryapp.domain.follow.controller;

import com.sparta.fooddeliveryapp.domain.follow.dto.FollowRequestDto;
import com.sparta.fooddeliveryapp.domain.follow.dto.FollowResponseDto;
import com.sparta.fooddeliveryapp.domain.follow.entity.Follow;
import com.sparta.fooddeliveryapp.domain.follow.service.FollowService;
import com.sparta.fooddeliveryapp.domain.user.entity.User;
import com.sparta.fooddeliveryapp.domain.user.repository.UserRepository;
import com.sparta.fooddeliveryapp.global.common.ResponseDto;
import com.sparta.fooddeliveryapp.global.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follow")
public class FollowController {

    private final FollowService followService;

    @PostMapping
    public ResponseEntity<ResponseDto> addFollow(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody FollowRequestDto followRequestDto
    ) {
        followService.addUserLike(userDetails.getUser(), followRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(HttpStatus.OK)
                        .message("팔로우 완료")
                        .build());
    }

    @DeleteMapping
    public ResponseEntity<ResponseDto> deleteFollow(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody FollowRequestDto followRequestDto
    ){
        followService.deleteUserLike(userDetails.getUser(), followRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(HttpStatus.OK)
                        .message("팔로우 취소")
                        .build());
    }

    @GetMapping
    public ResponseEntity<ResponseDto> getFollowList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size
    ) {
        List<FollowResponseDto> followList = followService.getFollowList(userDetails.getUser(), page, size);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.builder()
                        .status(HttpStatus.OK)
                        .message("팔로우 조회 성공")
                        .data(followList)
                        .build());
    }
}
