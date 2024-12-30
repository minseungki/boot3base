package com.example.demo.config;

import io.swagger.v3.oas.models.tags.Tag;

public class TagsConfig {

    public static final String TAG_COM_01 = "00_공통";
//    public static final Tag COMMON_TAG = new Tag(TAG_COM_01,"공통 전용 API 입니다.");
    public static final String TAG_LOGIN_01 = "01_회원/로그인";

    public static final Tag[] TAGS = new Tag[] {
//            new Tag(TAG_LOGIN_01, "회원/로그인 API 입니다."),
    };
}
