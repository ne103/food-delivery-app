package com.sparta.fooddeliveryapp.domain.user.service;

import com.sparta.fooddeliveryapp.domain.user.dto.*;
import com.sparta.fooddeliveryapp.domain.user.entity.UsedPassword;
import com.sparta.fooddeliveryapp.domain.user.entity.User;
import com.sparta.fooddeliveryapp.domain.user.entity.UserRoleEnum;
import com.sparta.fooddeliveryapp.domain.user.entity.UserStatusEnum;
import com.sparta.fooddeliveryapp.domain.user.repository.ReviewLikeRepository;
import com.sparta.fooddeliveryapp.domain.user.repository.StoreLikeRepository;
import com.sparta.fooddeliveryapp.domain.user.repository.UsedPasswordRepository;
import com.sparta.fooddeliveryapp.domain.user.repository.UserRepository;
import com.sparta.fooddeliveryapp.global.error.exception.*;
import com.sparta.fooddeliveryapp.global.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j(topic = "UserService")
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsedPasswordRepository usedpasswordRepository;
    private final JwtUtil jwtUtil;
    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";
    private final UsedPasswordRepository usedPasswordRepository;
    private final StoreLikeRepository storeLikeRepository;
    private final ReviewLikeRepository reviewLikeRepository;


    public void signup(SignupRequestDto signupRequestDto) {
        String loginId = signupRequestDto.getLoginId();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());
        String name = signupRequestDto.getName();
        String nickname = signupRequestDto.getNickname();
        String address = signupRequestDto.getAddress();
        String phone = signupRequestDto.getPhone();
        String email = signupRequestDto.getEmail();
        String intro = signupRequestDto.getIntro();
        UserRoleEnum role = signupRequestDto.getRole();
        UserStatusEnum status = UserStatusEnum.ACTIVE;

        // 중복 체크
        Optional<User> checkLoginId = userRepository.findByLoginId(loginId);
        if(checkLoginId.isPresent()){
            throw new DuplicateLoginIdException();
        }
        Optional<User> checkPhone = userRepository.findByPhone(phone);
        if(checkPhone.isPresent()){
            throw new DuplicatePhoneException();
        }
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if(checkEmail.isPresent()){
            throw new DuplicateEmailException();
        }

        // admin token 확인
        if (role == UserRoleEnum.ADMIN) {
            if (!ADMIN_TOKEN.equals(signupRequestDto.getAdminToken())) {
                throw new WrongAdminTokenException();
            }
        }

        User user = new User(loginId, password, name, nickname, address, phone, email, intro, role, status);

        UsedPassword newPassword = new UsedPassword();
        newPassword.setUser(user);
        newPassword.setPassword(password);
        List<UsedPassword> pwdList = new ArrayList<>();
        pwdList.add(newPassword);
        user.setUsedPasswordList(pwdList);

        userRepository.save(user);

        log.info("회원가입 완료");
    }

    @Transactional
    public void deactivateUser(DeactivateRequestDto requestDto, User user) {
        User tempUser = loadUserByLoginId(user.getLoginId());
        if(!( passwordEncoder.matches(requestDto.getPassword(), tempUser.getPassword()) )) {
            throw new WrongPasswordException();
        }
        tempUser.setStatusDeactivated();
        tempUser.setRefreshToken(null);
        log.info("user deactivated");
    }

    @Transactional
    public void updateProfile(UpdateProfileRequestDto requestDto, User user, Long userId) {
        // 어드민 및 일반 사용자의 권한 확인 : 아무나 수정 vs 본인만 수정
        if(user.getRole().equals(UserRoleEnum.ADMIN)){
            User userToUpdate = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
            if(requestDto.getName() != null) {
                userToUpdate.updateName(requestDto.getName());
            }
            if(requestDto.getNickname() != null) {
                userToUpdate.updateNickname(requestDto.getNickname());
            }
            if(requestDto.getAddress() != null) {
                userToUpdate.updateAddress(requestDto.getAddress());
            }
            if(requestDto.getIntro() != null) {
                userToUpdate.updateIntro(requestDto.getIntro());
            }
        }else {
            if(!user.getUserId().equals(userId)){
                throw new UserAuthorityDeniedException();
            }
            User selfUpdateUser = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
            if (requestDto.getName() != null) {
                selfUpdateUser.updateName(requestDto.getName());
            }
            if (requestDto.getNickname() != null) {
                selfUpdateUser.updateNickname(requestDto.getNickname());
            }
            if (requestDto.getAddress() != null) {
                selfUpdateUser.updateAddress(requestDto.getAddress());
            }
            if (requestDto.getIntro() != null) {
                selfUpdateUser.updateIntro(requestDto.getIntro());
            }
        }
        log.info("profile updated");
    }

    public User loadUserByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId).orElseThrow(UserNotFoundException::new);
    }

    public ProfileResponseDto getProfile(User user) {
        Long userId = user.getUserId();
        String name = user.getName();
        String nickname = user.getNickname();
        String address = user.getAddress();
        String phone = user.getPhone();
        String email = user.getEmail();
        String intro = user.getIntro();

        int likedStoresCount = storeLikeRepository.countByUser_UserId(userId);
        int likedReviewsCount = reviewLikeRepository.countByUser_UserId(userId);

        ProfileResponseDto result = new ProfileResponseDto(userId, name, nickname, address, phone, email, intro, likedStoresCount, likedReviewsCount);
        log.info("프로필 조회 완료");
        return result;
    }


    public List<UserSearchResponseDto> UserSearch(String nickname) {
        List<User> searchedUsers = userRepository.findAllByNickname(nickname);
        List<UserSearchResponseDto> result =new ArrayList<>();
        for(User user : searchedUsers){
            String email = user.getEmail();
            String intro = user.getIntro();
            UserStatusEnum status = user.getStatus();
            result.add(new UserSearchResponseDto(nickname, email, intro, status));
        }
        log.info("유저 조회 완료");
        return result;
    }

    @Transactional
    public void logout(Long userId) {
        // 전에 만들었던 내용 없애고 유저의 refresh token 만 Null 처리한다. -> Auth filter 에서 로그아웃 유저 거르기 위함
        User user = userRepository.findById(userId).orElseThrow(
            () -> new NullPointerException("존재하지 않는 사용자입니다")
        );
        user.setRefreshToken(null);
        log.info("logout success");
    }

    @Transactional
    public void updatePassword(UpdatePasswordRequestDto requestDto, User user) {
        User tempUser = loadUserByLoginId(user.getLoginId());

        if (!passwordEncoder.matches(requestDto.getOldPassword(), tempUser.getPassword())) {
            throw new WrongPasswordException();
        }

        // 가장 최근 4개의 비번을 가져온다(현재 비번 포함)
        List<UsedPassword> pwdList = usedPasswordRepository.findAllByUserId(tempUser.getUserId());

        log.info(String.valueOf(pwdList.size()));
        // Check if the new password has been used before
        for (UsedPassword pwd : pwdList) {
            if (passwordEncoder.matches(requestDto.getNewPassword(), pwd.getPassword())) {
                log.info("전에 4번 과 같은 비번");
                throw new PasswordUpdateFailException();
            }
        }

        String newPwd = passwordEncoder.encode(requestDto.getNewPassword());
        UsedPassword newPassword = new UsedPassword();
        newPassword.setUser(tempUser);
        newPassword.setPassword(newPwd);

        pwdList.add(newPassword);

        log.info("------");
        log.info(String.valueOf(pwdList.size()));
        if(pwdList.size() >= 5){
            Long pwdId = usedpasswordRepository.findOldestValueByUserId(tempUser.getUserId());
            usedPasswordRepository.deleteById(pwdId);
        }
        usedPasswordRepository.save(newPassword);
        tempUser.updatePassword(newPwd);
    }
}
