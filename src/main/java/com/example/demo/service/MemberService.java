package com.example.demo.service;

import com.example.demo.dto.common.enumeration.CustomErrorCode;
import com.example.demo.dto.login.LoginRequest;
import com.example.demo.dto.login.LoginResponse;
import com.example.demo.dto.login.LoginSelectResponse;
import com.example.demo.mapper.LoginMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.*;
import com.example.demo.dto.common.enumeration.MemberStatusCd;
import com.example.demo.dto.member.*;
import com.example.demo.exception.CustomException;
import com.example.demo.mapper.MemberMapper;
import com.example.demo.util.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private enum MemberERRCd implements CustomErrorCode {
        ERR_MEMBER_001("기존 등록된 회원 존재합니다."),
        ERR_MEMBER_002("이미 사용중인 아이디 입니다."),
        ERR_MEMBER_003("기존 비밀번호를 확인하세요."),
        ERR_MEMBER_004("기존 비밀번호 변경 불가"),
        ERR_MEMBER_005("탈퇴 회원 탈퇴 후 30일간 서비스 재가입 불가."),
        ERR_MEMBER_006("일치하는 회원 정보가 없는 경우"),
        ;
        private final String desc;
        public String getDesc() {
            return desc;
        }
        MemberERRCd(String desc) {
            this.desc = desc;
        }
    }

    private final Aes256Util aes256Util;
    private final Sha256Util sha256Util;

    private final MemberMapper mapper;
    private final LoginMapper loginMapper;
    private final LoginService loginService;


    @Transactional
    public LoginResponse insert(MemberInsertRequest req) {
        // BaseRequest 셋팅
        MapperUtil.setBaseRequest(req);

        // id 중복체크
        MemberSelectResponse selectMemberResponse = mapper.selectDuplicateMember(req);

        if (!ObjectUtils.isEmpty(selectMemberResponse)) {
            if (MemberStatusCd.SECESSION.getDesc().equals(selectMemberResponse.getMemberStatusCd().getDesc())) {
                // 탈퇴 회원의 경우 탈퇴 후 30일동안 가입불가
                throw new CustomException(MemberERRCd.ERR_MEMBER_005.toString(), MemberERRCd.ERR_MEMBER_005.getDesc());
            } else if (req.getId().equals(selectMemberResponse.getId())) {
                // ID 중복
                throw new CustomException(MemberERRCd.ERR_MEMBER_002.toString(), MemberERRCd.ERR_MEMBER_002.getDesc());
            }
        }

        if (StringUtils.hasText(req.getMobilePhoneNo())) {
            req.setMobilePhoneNo(aes256Util.encrypt(req.getMobilePhoneNo()));
        }

        if (StringUtils.hasText(req.getEmail())) {
            req.setEmail(aes256Util.encrypt(req.getEmail()));
        }

        if (StringUtils.hasText(req.getPassword())) {
            req.setPassword(sha256Util.encrypt(req.getPassword()));
        }

        req.setRegisterSeq(0L);
        req.setUpdaterSeq(0L);
        req.setMemberStatusCd(MemberStatusCd.NORMAL);
        mapper.insertMember(req); // 등록처리

        // 로그인 처리
        LoginSelectResponse member = loginMapper.selectLogin(LoginRequest.builder().id(req.getId()).build());
        return loginService.responseLogin(member, true);
    }

}
