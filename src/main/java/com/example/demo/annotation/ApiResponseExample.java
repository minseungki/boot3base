package com.example.demo.annotation;

import java.lang.annotation.*;

/**
 * <pre>
 * ApiResponseExample(errorCode = "ERR_LOGIN_002"),
 * ApiResponseExample(httpStatus = "401", enumName = "ErrorCode", errorCode = "ERR401_002")
 *
 * -> errorCode 만 작성 하는 경우는 http status : 400, errorCode : 해당 서비스 의 error code enum
 * -> httpStatus 도 같이 작성 하는 경우는 작성한 enum 의 errorCode 조회 ( 현재는 ErrorCode 만 기본 적용 상태 -> 에러 코드 추가 시 개발 필요 )
 *
 * exampleName : swagger Response 영역의 Examples 셀렉트 박스에 노출 되는 정보
 * httpStatus : {400, 401, 404, 500, 503} ( status 추가 시 개발 필요 )
 * </pre>
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ApiResponseExample {
    String httpStatus() default "";
    String exampleName() default "";
    String enumName() default "";
    String errorCode();
}
