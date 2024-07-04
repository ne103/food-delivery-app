package com.sparta.fooddeliveryapp.domain.like.controller;

import com.sparta.fooddeliveryapp.domain.like.dto.UserLikeRequestDto;
import com.sparta.fooddeliveryapp.domain.like.dto.UserLikeResponseDto;
import com.sparta.fooddeliveryapp.domain.like.entity.UserLike;
import com.sparta.fooddeliveryapp.domain.like.service.UserLikeService;
import com.sparta.fooddeliveryapp.global.common.ResponseDto;
import com.sparta.fooddeliveryapp.global.security.UserDetailsImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/like")
public class UserLikeController {

    private final UserLikeService userLikeService;

    @PostMapping
    public ResponseEntity<ResponseDto> addUserLike(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody UserLikeRequestDto userLikeRequestDto
    ) {
        UserLike userLike = userLikeService.addUserLike(userDetails.getUser(), userLikeRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(HttpStatus.OK)
                        .message("좋아요 완료")
                        .build());
    }

    @DeleteMapping
    public ResponseEntity<ResponseDto> deleteUserLike(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody UserLikeRequestDto userLikeRequestDto
    ) {
        userLikeService.deleteUserLike(userDetails.getUser(), userLikeRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(HttpStatus.OK)
                        .message("좋아요 취소 완료")
                        .build());
    }

    @GetMapping
    public ResponseEntity<ResponseDto> getUserLike(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam UserLikeRequestDto userLikeRequestDto,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size
    ) {
        List<UserLikeResponseDto> userLikeList = userLikeService.getUserLike(userDetails.getUser(), userLikeRequestDto, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseDto.builder()
                .status(HttpStatus.OK)
                .message("좋아요 조회 성공")
                .data(userLikeList)
                .build());
    }

    @GetMapping("/review")
    public ResponseEntity<ResponseDto> getLikedReview(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size
    ) {
        List<UserLikeResponseDto> likedComments = userLikeService.getLikedReview(userDetails.getUser(), page, size);
        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseDto.builder()
                .status(HttpStatus.OK)
                .message("좋아요한 리뷰 조회 성공")
                .data(likedComments)
                .build());
    }
}
