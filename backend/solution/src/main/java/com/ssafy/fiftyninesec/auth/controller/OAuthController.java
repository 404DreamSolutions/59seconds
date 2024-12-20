package com.ssafy.fiftyninesec.auth.controller;

import com.ssafy.fiftyninesec.auth.dto.OAuthResponseDto;
import com.ssafy.fiftyninesec.auth.service.OAuthService;
import com.ssafy.fiftyninesec.global.util.JwtUtil;
import com.ssafy.fiftyninesec.solution.entity.Member;
import com.ssafy.fiftyninesec.solution.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @GetMapping("/kakao/callback")
    public ResponseEntity<OAuthResponseDto> kakaoCallback(@RequestParam("code") String code, HttpServletResponse response) {
        ArrayList<String> tokens = oAuthService.getKakaoTokens(code);
        String idToken = tokens.get(2);
        
        String kakaoSub = jwtUtil.getSubFromIdToken(idToken);
    
        Member member = memberRepository.findByKakaoSub(kakaoSub);

        return ResponseEntity.ok(oAuthService.loginOrRegister(member, kakaoSub, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        oAuthService.logout(request, response);
        return ResponseEntity.ok().build();
    }
}
