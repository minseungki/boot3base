package com.example.demo.config;

import com.example.demo.annotation.ApiResponseExample;
import com.example.demo.annotation.ApiResponseExamples;
import com.example.demo.dto.common.enumeration.CustomErrorCode;
import com.example.demo.dto.common.enumeration.ErrorCode;
import com.example.demo.util.RestUtil;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Configuration
@Getter
public class SpringDocConfig {

    @Value("${jwt.header}")
    private String HEADER_AUTH;

    @Value("${jwt.header-refresh}")
    private String HEADER_AUTH_REFRESH;

    @Value("${jwt.prefix}")
    private String HEADER_PREFIX;

    private final String UNAUTHORIZED_DESCRIPTION = "인증 오류";
    private final String FALSIFY_DESCRIPTION = ErrorCode.ERR401_002.getDesc();

    private static final String MEDIA_TYPE = "application/json";

    // 기본 OpenAPI 정보 설정
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .openapi("3.0.0")
            .info(new Info().title("데모 프로젝트 REST API 문서")
                .version("1.0.0")
                .description(getDescription())) // description
            .components(new Components()
                .addSecuritySchemes(HEADER_AUTH, new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("Bearer")
                    .bearerFormat("JWT")
                    .name(HEADER_AUTH)
                    .description("Enter your JWT token")
                )) // 공통 헤더 추가 (Authorize 버튼 생성)
            .addSecurityItem(new SecurityRequirement().addList(HEADER_AUTH)) // Swagger UI에서 모든 API 요청 시 Authorization 헤더를 추가할 수 있도록 설정
            .tags(TagsConfig.TAGS) // 등록된 태그 불러 오기
            ;
    }

    @Bean
    public OperationCustomizer customizeResponses() {
        return (operation, handlerMethod) -> {
            ApiResponses responses = operation.getResponses();

            LinkedHashMap<Object, Object> badRequestErrorMap = new LinkedHashMap<>();
            LinkedHashMap<Object, Object> unauthorizedErrorMap = new LinkedHashMap<>();
            LinkedHashMap<Object, Object> customUnauthorizedErrorMap = new LinkedHashMap<>();
            LinkedHashMap<Object, Object> forbiddenErrorMap = new LinkedHashMap<>();
            LinkedHashMap<Object, Object> notFoundErrorMap = new LinkedHashMap<>();
            LinkedHashMap<Object, Object> serverErrorMap = new LinkedHashMap<>();
            LinkedHashMap<Object, Object> serviceUnavailableErrorMap = new LinkedHashMap<>();

            // 컨트롤러에 정의된 400 에러 수집
            ApiResponseExamples customSwaggerExamples = handlerMethod.getMethodAnnotation(ApiResponseExamples.class);
            if (customSwaggerExamples != null) {
                ApiResponseExample[] examples = customSwaggerExamples.examples();
                for (ApiResponseExample example : examples) {
                    String httpStatus = example.httpStatus();
                    String exampleName = "";
                    String enumName = "";
                    String errorCode = example.errorCode();
                    String message = "";
                    String desc = "";

                    if (StringUtils.hasText(example.enumName())) {
                        if ("ErrorCode".equals(example.enumName())) {
                            ErrorCode errorCodeEnum = ErrorCode.valueOf(errorCode);
                            message = getResponse(errorCodeEnum.getCode(), errorCodeEnum.getMessage(), null);
                            desc = errorCodeEnum.getDesc();
                        }
                    } else {
                        String beanName = handlerMethod.getBeanType().getName();
                        String servicePackage = beanName.replace("controller", "service").replace("Controller", "Service");
                        enumName = String.join("", beanName.replace("com.example.demo.controller.", "").replace("Controller", ""), "ERRCd");
                        String enumPackage = String.join("$", servicePackage, enumName);
                        CustomErrorCode enumByCode = RestUtil.getEnumByCode(enumPackage, example.errorCode());
                        if (enumByCode != null) {
                            desc = enumByCode.getDesc();
                            message = getResponse(example.errorCode(), desc, null);
                        }
                    }

                    exampleName = StringUtils.hasText(example.exampleName()) ? example.exampleName() : desc;

                    if (StringUtils.hasText(httpStatus)) {
                        if ("401".equals(httpStatus)) {
                            // 엑세스 토큰 갱신 api 경우 custom 401에러 추가
                            customUnauthorizedErrorMap.put(exampleName, new Example().value(message));
                        }
                    } else {
                        badRequestErrorMap.put(exampleName, new Example().value(message));
                    }
                }
            }

            // 공통코드 수집
            for (ErrorCode errorCode : ErrorCode.values()) {
                String status = errorCode.getCode().replace("ERR", "").split("_")[0];
                if ("400".equals(status)) {
                    badRequestErrorMap.put(errorCode.getDesc(), new Example().value(getResponse(errorCode.getCode(), errorCode.getMessage(), null)));
                } else if ("401".equals(status)) {
                    unauthorizedErrorMap.put(errorCode.getDesc(), new Example().value(getResponse(errorCode.getCode(), errorCode.getMessage(), null)));
                } else if ("403".equals(status)) {
                    forbiddenErrorMap.put(errorCode.getDesc(), new Example().value(getResponse(errorCode.getCode(), errorCode.getMessage(), null)));
                } else if ("404".equals(status)) {
                    notFoundErrorMap.put(errorCode.getDesc(), new Example().value(getResponse(errorCode.getCode(), errorCode.getMessage(), null)));
                } else if ("500".equals(status)) {
                    serverErrorMap.put(errorCode.getDesc(), new Example().value(getResponse(errorCode.getCode(), errorCode.getMessage(), null)));
                } else if ("503".equals(status)) {
                    serviceUnavailableErrorMap.put(errorCode.getDesc(), new Example().value(getResponse(errorCode.getCode(), errorCode.getMessage(), null)));
                }
            }

            addApiResponse(responses, "400", "잘못된 요청", badRequestErrorMap);

            // login / member controller 경우 401 제외
            if (!handlerMethod.getBean().toString().contains("login") && !handlerMethod.getBean().toString().contains("member")) {
                addApiResponse(responses, "401", UNAUTHORIZED_DESCRIPTION, unauthorizedErrorMap);
            }

            // 엑세스 토큰 갱신 api 경우 custom 401에러 추가(LoginController에 있음)
            if ("updateAccessToken".equals(operation.getOperationId())) {
                addApiResponse(responses, "401", UNAUTHORIZED_DESCRIPTION, customUnauthorizedErrorMap);
            }

            // 권한 체크 어노테이션이 있는 경우 403 에러 추가
            PreAuthorize preAuthorizeMethodAnnotation = handlerMethod.getMethodAnnotation(PreAuthorize.class);
            if (!ObjectUtils.isEmpty(preAuthorizeMethodAnnotation)) {
                addApiResponse(responses, "403", "권한 오류", forbiddenErrorMap);
            }

            if (checkDetailApi(handlerMethod)) {
                addApiResponse(responses, "404", "No Data 오류", notFoundErrorMap);
            }

            addApiResponse(responses, "500", "서버 내부 오류", serverErrorMap);
            addApiResponse(responses, "503", "시스템 점검 오류", serviceUnavailableErrorMap);

            // Authorization-Refresh 헤더일 경우 Bearer 자동으로 붙임
            if (operation.getParameters() != null) {
                for (io.swagger.v3.oas.models.parameters.Parameter parameter : operation.getParameters()) {
                    if (HEADER_AUTH_REFRESH.equals(parameter.getName())) {
                        parameter.setSchema(new io.swagger.v3.oas.models.media.StringSchema()._default(HEADER_PREFIX));
                    }
                }
            }

            return operation;
        };
    }

    private String getDescription() {
        String updateDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String updateView = "<br><h1>ℹ️ 서버 업데이트 일자 : " + updateDate + "</h1>";
        String guide = "<h1>\uD83D\uDCDD 아래 API 가이드를 열어 내용을 확인해주세요 </h1><br>";
        String guideObject = ""
            + "<blockquote>"

                + "<details> <summary>기본 응답 객체</summary>\n\n"
                + "<blockquote>\n\n"
                + "| 객체명  | 필수여부 | 타입              | 설명                   |\n"
                + "|---------|----------|-------------------|------------------------|\n"
                + "| code    | Y        | String            | 에러코드               |\n"
                + "| message | Y        | String            | 에러메시지             |\n"
                + "| data    | N        | JSON / JSON Array | 결과데이터 / 에러 상세 |\n"
                + "</blockquote>"
                + "</details>\n\n"

                + "<details> <summary>공통 에러 코드</summary>\n\n"
                + "<blockquote>\n\n"
                + "| HTTP 코드 | 에러 코드  | 에러메시지                                             | 조치방안                                   |\n"
                + "|-----------|------------|--------------------------------------------------------|--------------------------------------------|\n"
                + "| 400       | ERR400_001 | 오류가 발생했습니다. 관리자에게 문의 하세요.           | -                                          |\n"
                + "| 400       | ERR400_002 | 유효성 검사에서 에러가 발생하였습니다.                 | 파라미터 타입 및 필수 여부 확인            |\n"
                + "| 401       | ERR401_001 | 토큰이 만료되었습니다.                                 | 발급받은 리프레시 토큰으로 액세스토큰 갱신 |\n"
                + "| 401       | ERR401_002 | 토큰이 위변조되었습니다.                               | 소유한 토큰을 삭제하고 로그인 페이지 이동  |\n"
                + "| 401       | ERR401_003 | 토큰은 필수값입니다.                                   | -                                          |\n"
                + "| 401       | ERR401_999 | 토큰 관련 오류가 발생했습니다. 관리자에게 문의 하세요. | -                                          |\n"
                + "| 500       | ERR500_001 | 서버 내부 오류                                         | -                                          |\n"
                + "| 503       | ERR503_001 | 시스템 점검 오류                                       | 안내 메시지 노출                           |\n"
                + "</blockquote>"
                + "</details>\n\n"

                + "<details> <summary>기타 에러 코드</summary>\n\n"
                + "<blockquote>\n\n"
                + "| HTTP 코드 | 에러 코드  | 에러메시지     | 조치방안                                          |\n"
                + "|-----------|------------|----------------|---------------------------------------------------|\n"
                + "| 403       | ERR403_001 | 접근이 거부되었습니다. | 권한 획득 요청 |\n"
                + "| 404       | ERR404_001 | DATA NOT FOUND | 없는 시퀀스 정보 이거나 삭제된 페이지 에러 메시지 노출 |\n"
                + "</blockquote>"
                + "</details>\n\n"

                + "<details> <summary>인증 방식</summary>\n\n"
                + "- 클라이언트는 API 요청 시, 로그인 API를 통해 발급 받은 JWT를 Authorization 헤더에 Bearer 방식으로 포함하여 서버에 전달합니다.\n\n"
                + ">```\n"
                + ">GET /api/bbs\n"
                + ">Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\n"
                + ">```\n\n"
                + "- Swagger의 경우 개발 편의를 위해 Bearer는 생략합니다.\n\n"
                + "</details>\n\n"

                + "<details> <summary>Swagger 사용법</summary>\n\n"
                + "<a href=\"https://github.com/minseungki/boot3base/blob/main/doc/client.md\" target=\"_blank\">클라이언트 가이드 바로가기</a>\n\n"
                + "<a href=\"https://github.com/minseungki/boot3base/blob/main/doc/backend.md\" target=\"_blank\">백엔드 가이드 바로가기</a>\n\n"
                + "</details>\n\n"

            + "</blockquote>"
            ;
        return updateView + guide + "<details> <summary>가이드 보기</summary>\n\n" + guideObject + "</details>";
    }

    private String getResponse(String code, String message, String data) {
        return "{\n"
                + "    \"code\" : \"" + code + "\",\n"
                + "    \"message\" : \"" + message + "\"" + (StringUtils.hasText(data) ? "," : "") + "\n"
                + (StringUtils.hasText(data) ? "    \"data\" : " + data + "\n" : "")
                + "}\n";
    }

    private void addApiResponse(ApiResponses responses, String status, String description, String example) {
        responses.addApiResponse(status, new ApiResponse()
                .description(description)
                .content(new Content().addMediaType(MEDIA_TYPE,
                        new MediaType().schema(new Schema<>().example(example))
                ))
        );
    }

    private void addApiResponse(ApiResponses responses, String status, String description, LinkedHashMap example) {
        responses.addApiResponse(status, new ApiResponse()
                .description(description)
                .content(new Content().addMediaType(MEDIA_TYPE,
                        new MediaType().examples(example)
                ))
        );
    }

    private boolean checkDetailApi(HandlerMethod handlerMethod) {
        boolean isDetail = false;
        boolean isGetMethod = handlerMethod.hasMethodAnnotation(GetMapping.class);
        if (isGetMethod) {
            for (Parameter parameter : handlerMethod.getMethod().getParameters()) {
                boolean isPathVariable = false;
                for (Annotation annotation : parameter.getAnnotations()) {
                    if (annotation.annotationType().equals(PathVariable.class)) {
                        isPathVariable = true;
                        break;
                    }
                }
                boolean isSeq = parameter.getName().toLowerCase().contains("seq");
                if (isPathVariable && isSeq) {
                    isDetail = true;
                    break;
                }
            }
        }
        return isDetail;
    }
}
