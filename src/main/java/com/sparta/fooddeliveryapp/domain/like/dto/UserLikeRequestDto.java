package com.sparta.fooddeliveryapp.domain.like.dto;

import com.sparta.fooddeliveryapp.domain.like.entity.UserLikeType;
import lombok.Getter;

@Getter
public class UserLikeRequestDto {

    private UserLikeType userLikeType;

    private Long typeId;

}
