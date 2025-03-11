package com.example.demo.config;

import io.swagger.v3.oas.models.tags.Tag;

import java.util.Arrays;
import java.util.List;

public class TagsConfig {

    public static final String TAG_COM_01 = "00_공통";
    public static final String TAG_UTIL_01 = "00_시스템점검";
    public static final String TAG_LOGIN_01 = "01_회원/로그인";
    public static final String TAG_BBS_01 = "02_게시판";

    public static final List<Tag> TAGS = Arrays.asList(
        new Tag().name(TAG_COM_01).description("공통 API 입니다."),
        new Tag().name(TAG_UTIL_01).description("시스템점검 API 입니다."),
        new Tag().name(TAG_LOGIN_01).description("회원/로그인 API 입니다."),
        new Tag().name(TAG_BBS_01).description("게시판 API 입니다.")
    );

}
