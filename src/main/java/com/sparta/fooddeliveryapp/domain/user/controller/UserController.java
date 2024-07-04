package com.sparta.fooddeliveryapp.domain.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.fooddeliveryapp.domain.user.dto.DeactivateRequestDto;
import com.sparta.fooddeliveryapp.domain.user.dto.ProfileResponseDto;
import com.sparta.fooddeliveryapp.domain.user.dto.SignupRequestDto;
import com.sparta.fooddeliveryapp.domain.user.dto.UpdatePasswordRequestDto;
import com.sparta.fooddeliveryapp.domain.user.dto.UpdateProfileRequestDto;
import com.sparta.fooddeliveryapp.domain.user.dto.UserSearchResponseDto;
import com.sparta.fooddeliveryapp.domain.user.entity.User;
import com.sparta.fooddeliveryapp.domain.user.service.KakaoService;
import com.sparta.fooddeliveryapp.domain.user.service.UserService;
import com.sparta.fooddeliveryapp.global.common.ResponseDto;
import com.sparta.fooddeliveryapp.global.security.JwtUtil;
import com.sparta.fooddeliveryapp.global.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j(topic = "Users Controller")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
  
    private final KakaoService kakaoService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseDto> signup(@RequestBody SignupRequestDto requestDto) {
        userService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(HttpStatus.OK)
                        .message("회원가입 완료")
                        .build());
    }

    @PutMapping("/deactivate")
    public ResponseEntity<ResponseDto> deactivateUser(@RequestBody DeactivateRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        userService.deactivateUser(requestDto, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(HttpStatus.OK)
                        .message("회원 비활성화 완료")
                        .build());
    }

    @PatchMapping("/update-profile/{userId}")
    public ResponseEntity<ResponseDto> updateProfile(@RequestBody UpdateProfileRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long userId){
        userService.updateProfile(requestDto, userDetails.getUser(), userId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(HttpStatus.OK)
                        .message("프로필 수정 완료")
                        .build());
    }

    @GetMapping("/profile")
    public ResponseEntity<ResponseDto> getUserProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        ProfileResponseDto profileResponse = userService.getProfile(user);

        ProfileResponseDto responseDto = new ProfileResponseDto(
            profileResponse.getUserId(),
            profileResponse.getName(),
            profileResponse.getNickname(),
            profileResponse.getAddress(),
            profileResponse.getPhone(),
            profileResponse.getEmail(),
            profileResponse.getIntro(),
            profileResponse.getStoreLikeCount(),
            profileResponse.getReviewLikeCount()
        );

        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseDto.builder()
                .status(HttpStatus.OK)
                .message("프로필 조회 성공")
                .data(responseDto)
                .build());
    }

    @GetMapping("/profile/{nickname}")
    public List<UserSearchResponseDto> UserSearch(@PathVariable String nickname){
        List<UserSearchResponseDto> res = userService.UserSearch(nickname);
        return res;
    }

    @PutMapping("/update-password")
    public ResponseEntity<ResponseDto> updatePassword(@RequestBody UpdatePasswordRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        userService.updatePassword(requestDto, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(HttpStatus.OK)
                        .message("비밀번호 수정 완료")
                        .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDto> logout(@AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        userService.logout(userDetails.getUser().getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(HttpStatus.OK)
                        .message("로그아웃 완료")
                        .build());
    }
  
  @GetMapping("/kakao/callback")
    public ResponseEntity<String> kakaoLogin(
            @RequestParam String code,
            HttpServletResponse response
  ) throws JsonProcessingException, UnsupportedEncodingException {

        log.info(code);
        List<String> tokens = kakaoService.kakaoLogin(code, response);
        log.info("kakaoLogin");
//        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, token);
//        cookie.setPath("/");
//        response.addCookie(cookie);
        HttpStatus status = HttpStatus.OK;
      String htmlResponse = "<html>" +
              "<head>" +
              "<style>" +
              "body { font-family: 'Helvetica Neue', Arial, sans-serif; margin: 0; padding: 0; background-color: #121212; color: #e0e0e0; }" +
              ".container { max-width: 600px; margin: 40px auto; padding: 20px; background-color: #1e1e1e; border-radius: 10px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2); }" +
              ".header { text-align: center; margin-bottom: 20px; }" +
              ".header h1 { margin: 0; font-size: 28px; color: #76c7c0; }" +
              ".content { margin-bottom: 20px; }" +
              ".content p { margin: 10px 0; font-size: 18px; color: #bbbbbb; }" +
              ".token { word-wrap: break-word; background-color: #2d2d2d; padding: 15px; border: 1px solid #444; border-radius: 5px; font-family: 'Courier New', Courier, monospace; color: #b3e5fc; }" +
              ".status { font-weight: bold; color: #81c784; }" +
              ".message { font-weight: bold; color: #e57373; }" +
              "a { color: #81d4fa; text-decoration: none; }" +
              "a:hover { text-decoration: underline; }" +
              "@media (max-width: 600px) { .container { padding: 15px; } }" +
              "</style>" +
              "</head>" +
              "<body>" +
              "<div class='container'>" +
              "<div class='header'>" +
              "<h1>카카오 로그인 결과</h1>" +
              "</div>" +
              "<div class='content'>" +
              "<p><strong>Status:</strong> <span class='status'>" + status.value() + "</span></p>" +
              "<p><strong>Message:</strong> <span class='message'>" + status.name() + "</span></p>" +
              "<p><strong>" + JwtUtil.AUTHORIZATION_HEADER + ":</strong></p>" +
              "<p class='token'>" + tokens.get(0) + "</p>" +
              "<p class='token'>" + tokens.get(1) + "</p>" +
              "</div>" +
              "<div class='footer'>" +
              "<p>For more details, visit our <a href='https://d2u3dcdbebyaiu.cloudfront.net/uploads/atch_img/72/56ed10ccf8c3b810cc3dbbc09e9b30e6_res.jpeg'>help center</a>.</p>" +
              "</div>" +
              "</div>" +
              "</body>" +
              "</html>";
        return ResponseEntity.status(HttpStatus.OK).body(htmlResponse);
    }
    @GetMapping("/kakao/setToken")
    public String setToken(@RequestParam String token, HttpServletResponse response) {
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);
        return "토큰 교환 성공";
    }
    @GetMapping("/kakao")
    public String kakaoLoginPage() {
        return "https://kauth.kakao.com/oauth/authorize?client_id=f05a3acaceda30592f534db56647e0e8&redirect_uri=https://localhost:443/api/users/kakao/callback&response_type=code";
    }
}
