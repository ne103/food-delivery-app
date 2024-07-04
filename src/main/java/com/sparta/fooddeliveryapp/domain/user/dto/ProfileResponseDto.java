package com.sparta.fooddeliveryapp.domain.user.dto;

import lombok.Getter;

@Getter
public class ProfileResponseDto {

    private final Long userId;
    private final String name;
    private final String nickname;
    private final String address;
    private final String phone;
    private final String email;
    private final String intro;
    private final int storeLikeCount;
    private final int reviewLikeCount;

    public ProfileResponseDto(Long userId, String name, String nickname, String address, String phone,
                              String email, String intro, int storeLikeCount, int reviewLikeCount) {
        this.userId = userId;
        this.name = name;
        this.nickname = nickname;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.intro = intro;
        this.storeLikeCount = storeLikeCount;
        this.reviewLikeCount = reviewLikeCount;
    }

}
