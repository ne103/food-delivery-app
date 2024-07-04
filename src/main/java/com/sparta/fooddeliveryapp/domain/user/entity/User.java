package com.sparta.fooddeliveryapp.domain.user.entity;

import com.sparta.fooddeliveryapp.global.common.TimeStamped;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "login_id", nullable = false)
    private String loginId;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
    private List<UsedPassword> usedPasswordList = new ArrayList<>();

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "intro")
    private String intro;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRoleEnum role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatusEnum status;

    @Setter
    @Column(name = "refresh_token")
    private String refreshToken;

    @Setter
    @Column(name = "kakao_id")
    private Long kakaoId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
    private List<StoreLike> storeLikes = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
    private List<ReviewLike> reviewLikes = new ArrayList<>();

    private int storeLikeCount;
    private int reviewLikeCount;

    @PostPersist
    @PostUpdate
    public void updateLikeCounts() {
        this.storeLikeCount = this.storeLikes.size();
        this.reviewLikeCount = this.reviewLikes.size();
    }

    public void setUsedPasswordList(List<UsedPassword> usedPasswordList) {
        this.usedPasswordList.clear();
        if (usedPasswordList != null) {
            this.usedPasswordList.addAll(usedPasswordList);
        }
    }

    public void setStatusDeactivated() {
        this.status = UserStatusEnum.DEACTIVATED;
    }

    public void updateName(String name){this.name = name;}
    public void updateNickname(String nickname){this.nickname = nickname;}
    public void updateAddress(String address){this.address = address;}
    public void updateIntro(String intro){this.intro = intro;}
    public void updatePassword(String password){this.password = password;}

    public User(String loginId, String password, String name, String nickname, String address, String phone,
        String email, String intro, UserRoleEnum role, UserStatusEnum status) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.intro = intro;
        this.role = role;
        this.status = status;
    }
}