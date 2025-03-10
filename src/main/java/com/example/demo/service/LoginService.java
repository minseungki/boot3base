package com.example.demo.service;

import com.example.demo.dto.common.JwtAuthenticationVo;
import com.example.demo.dto.common.RedisAuthenticationVo;
import com.example.demo.dto.common.enumeration.CustomErrorCode;
import com.example.demo.dto.common.enumeration.JwtType;
import com.example.demo.dto.common.enumeration.PlatformType;
import com.example.demo.dto.login.*;
import com.example.demo.exception.CustomException;
import com.example.demo.mapper.LoginMapper;
import com.example.demo.util.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    @Getter
    @AllArgsConstructor
    private enum LoginERRCd implements CustomErrorCode {
        ERR_LOGIN_001("일치하는 회원 정보가 없는 경우"),
        ERR_LOGIN_002("리프레시 토큰이 없는 경우"),
        ;
        private final String desc;
    }

    @Value("${jwt.platform-type}")
    private String PLATFORM_TYPE;

    private final LoginMapper mapper;

    private final Sha256Util sha256Util;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;


    @Transactional
    public LoginResponse login(LoginRequest req) {
        // 회원 조회
        LoginSelectResponse member = mapper.selectLogin(req);

        if (ObjectUtils.isEmpty(member) || !sha256Util.encrypt(req.getPassword()).equals(member.getPassword())) {
            // 아이디가 없거나 비밀번호 오류
            throw new CustomException(LoginERRCd.ERR_LOGIN_001);
        } else {
            // 정상인 경우
            return responseLogin(member, true);
        }
    }

    public LoginResponse updateAccessToken(UpdateAccessTokenRequest req) {
        String refreshToken = jwtUtil.getRefreshToken();
        String accessToken = jwtUtil.getAccessToken();

        req.setRefreshToken(refreshToken);

        if (ObjectUtils.isEmpty(req.getRefreshToken())) {
            throw new CustomException(LoginERRCd.ERR_LOGIN_002);
        }

        // 토큰 유효성 검사
        jwtUtil.checkValidToken(accessToken, refreshToken);

        JwtAuthenticationVo jwtAuthenticationVo = jwtUtil.getTokenVo(req.getRefreshToken());
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setMemberSeq(jwtAuthenticationVo.getSeq());
        // 리프레시 토큰 만료일자가 15일보다 작으면 리프레시 토큰 자동 재발급
        boolean isGenerateRefresh = jwtUtil.checkRefreshExpiration(jwtAuthenticationVo.getExpiration());
        return responseLogin(mapper.selectMember(loginRequest), isGenerateRefresh);
    }

    @Transactional
    public LoginResponse responseLogin(LoginSelectResponse member, boolean isGenerateRefresh) {
        // 토큰 생성
        LoginResponse token = generateToken(member, isGenerateRefresh);

        // 비밀번호 만료여부 체크
        LocalDateTime now = LocalDateTime.now();
        boolean isPasswordExpired = ChronoUnit.DAYS.between(member.getPasswordChangeDt(), now) > 90;

        // 로그인 이력 저장
//        FrontLoginHistoryInsertRequest loginHistoryInsertRequest = new FrontLoginHistoryInsertRequest();
//        if (ObjectUtils.isEmpty(req.getDeviceInfo())) {
//            loginHistoryInsertRequest = FrontLoginHistoryInsertRequest.builder()
//                    .memberSeq(member.getMemberSeq())
//                    .build();
//        } else {
//            loginHistoryInsertRequest = FrontLoginHistoryInsertRequest.builder()
//                    .memberSeq(member.getMemberSeq())
//                    .appVersion(req.getDeviceInfo().getAppVersion())
//                    .osVersion(req.getDeviceInfo().getOsVersion())
//                    .build();
//        }
//
//        MapperUtil.setBaseRequest(loginHistoryInsertRequest);
//        loginHistoryInsertRequest.setRegisterSeq(member.getMemberSeq());
//        mapper.insertLoginHistory(loginHistoryInsertRequest);

        return LoginResponse.builder()
                .passwordExpiredYn(isPasswordExpired)
                .accessToken(token.getAccessToken())
                .accessTokenExpiredDt(token.getAccessTokenExpiredDt())
                .refreshToken(token.getRefreshToken())
                .refreshTokenExpiredDt(token.getRefreshTokenExpiredDt())
                .build();
    }

    private LoginResponse generateToken(LoginSelectResponse req, boolean isGenerateRefresh) {
        JwtAuthenticationVo jwtAuthenticationVo = JwtAuthenticationVo.builder()
                .seq(req.getMemberSeq())
                .id(req.getId())
                .jwtType(JwtType.access)
                .platformType(PlatformType.valueOf(PLATFORM_TYPE))
                .role(req.getRole())
                .build();

        // 엑세스 토큰 생성
        String accessToken = jwtUtil.generateToken(jwtAuthenticationVo);
        String accessTokenExpiredDt = jwtUtil.dateToDateFormatString(jwtAuthenticationVo.getExpiration());

        // 엑세스 토큰 builder 삽입
        LoginResponse.LoginResponseBuilder response = LoginResponse.builder()
                .accessToken(accessToken)
                .accessTokenExpiredDt(accessTokenExpiredDt);

        if (isGenerateRefresh) {
            // 리프레시 토큰 생성
            jwtAuthenticationVo = JwtAuthenticationVo.builder()
                    .seq(req.getMemberSeq())
                    .id(req.getId())
                    .jwtType(JwtType.refresh)
                    .platformType(PlatformType.valueOf(PLATFORM_TYPE))
                    .role(req.getRole())
                    .build();


            String refreshToken = jwtUtil.generateToken(jwtAuthenticationVo);
            String refreshTokenExpiredDt = jwtUtil.dateToDateFormatString(jwtAuthenticationVo.getExpiration());

            // 리프레시 토큰/회원 정보 Redis 저장
            redisUtil.setRedisUserInfo(RedisAuthenticationVo.builder()
                    .userId(req.getId())
                    .username(req.getName())
                    .password(req.getPassword())
                    .role(req.getRole())
                    .refreshToken(refreshToken)
                    .build());

            // 리프레시 토큰 builder 삽입
            response.refreshToken(refreshToken)
                    .refreshTokenExpiredDt(refreshTokenExpiredDt);
        }

        return response.build();
    }

}
