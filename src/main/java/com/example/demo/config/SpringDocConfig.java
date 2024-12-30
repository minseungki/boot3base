package com.example.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class SpringDocConfig {

//    @Value("${jwt.header}")
//    private String HEADER_AUTH;

    private final String UPDATE_DATE = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    private final String UPDATE_VIEW = "# 서버 업데이트 일자 : "+UPDATE_DATE+"<br><br>";
    private final String description = UPDATE_VIEW +
            "\n" +
            "## HTTP STATUS CODE\n" +
            "<details> <summary>내용보기</summary>\n\n"+
            "|status|description|\n" +
            "|:---:|:---|\n" +
            "|200|`'성공'`                    |\n" +
            "|400|`'요청 파라미터 에러 (코드 확인)'`   |\n" +
            "|401|`'인증 에러'`                 |\n" +
            "|500|`'서버 에러 -> 백엔드 문의'`   |\n" +

            "\n\n" +
            "</details>\n\n"+

            "## COMMON CODE\n" +
            "<details> <summary>내용보기</summary>\n\n"+
            "|status|code|description|\n" +
            "|:---:|:---|:---|\n" +
            "|200|SUC200_001|처리 완료|\n" +
            "|-|-|-|\n" +
            "|400|ERR400_001|파라미터 오류|\n" +

            "|401|ERR401_001|토큰만료|\n" +
            "|401|ERR401_002|토큰 위변조|\n" +
            "|401|ERR401_003|토큰 필수 값|\n" +
            "|401|ERR401_999|토큰 오류 서버 확인 필요|\n" +

            "|404|ERR404_001|FILE NOT FOUND|\n" +
            "|404|ERR404_002|DATA NOT FOUND|\n" +

            "|500|ERR500_001|Server Unchecked Exception (서버 에러) -> 백엔드 문의|\n" +

            "|503|ERR503_001|시스템점검(내용 포함)|\n" +
            "|503|ERR503_002|강제 앱 업데이트|\n" +
            "|503|ERR503_003|앱 버전 체크 에러|\n" +

            "</details>\n\n"+

            "## 개별 API ERROR CODE(API 별 오류, 아래 예, ERR_[테이블명]_001 )\n" +
            "<details> <summary>내용보기</summary>\n\n"+
            "|status|code|description|\n" +
            "|:---:|:---|:---|\n" +
            "|400|ERR_BBS_001|제목을 입력해주세요.|\n" +
            "|400|ERR_MEMBER_001|아이디는 필수 입력값입니다|\n" +
            "|400|ERR_MENU_001|메뉴명을 입력해주세요.|\n" +

            "</details>\n\n"
            ;

    // 기본 OpenAPI 정보 설정
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("데모 프로젝트 REST API 문서")
                        .version("v1")
                        .description(description))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("Bearer")
                                .bearerFormat("JWT")
                                .name("Authorization")
                                .description("Enter your JWT token")
                        ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

}