package com.sparta.fooddeliveryapp.domain.like.dto;

import com.sparta.fooddeliveryapp.domain.like.entity.UserLikeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserLikeResponseDto {

    private Long userId;

    private UserLikeType userLikeType;

    private Long typeId;
}
