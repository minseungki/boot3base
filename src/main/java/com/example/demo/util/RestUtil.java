package com.example.demo.util;

import com.example.demo.dto.common.enumeration.CustomErrorCode;
import com.example.demo.dto.common.enumeration.ErrorCode;
import com.example.demo.dto.common.enumeration.ErrorField;
import com.example.demo.dto.common.EmptyResponse;
import com.example.demo.dto.common.ResponseModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@Component
public class RestUtil {

    private static final ObjectMapper objectMapper; // ObjectMapper

    private static final String SUC_CODE = "SUC200_001";
    private static final String SUC_MSG = "처리가 완료되었습니다.";

    @Value("${jwt.header}")
    private String HEADER_AUTH_VALUE;

    @Value("${jwt.header-refresh}")
    private String HEADER_AUTH_REFRESH_VALUE;

    private static String HEADER_AUTH;
    private static String HEADER_AUTH_REFRESH;

    @PostConstruct
    public void init() {
        HEADER_AUTH = HEADER_AUTH_VALUE;
        HEADER_AUTH_REFRESH = HEADER_AUTH_REFRESH_VALUE;
    }

    private static final EmptyResponse emptyDto = new EmptyResponse();

    static { // ObjectMapper 생성 및 설정
        objectMapper = new ObjectMapper()
                /*
                 * convertValue 를 사용할 때
                 * fromValue 객체에는 있지만 toValueType 객체에는 없는 필드가 있으면 에러가 발생합니다.
                 * 해당 에러를 발생하지 않기 위해서
                 * fromValue 객체에는 있지만 toValueType 객체에는 없는 필드를 무시하도록 설정
                 */
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static <T> ResponseEntity<ResponseModel<EmptyResponse>> ok() {
        return ok(emptyDto, SUC_CODE, SUC_MSG);
    }
    public static <T> ResponseEntity ok(final T body) { return ok(body, SUC_CODE, SUC_MSG);}
    private static <T> ResponseEntity<ResponseModel<T>> ok(final T body, final String code, final String message) {
        ResponseModel<T> model = new ResponseModel<T>();

        model.setCode(code);
        model.setMessage(message);
        model.setData(body);

        return ResponseEntity
                .ok()
                .body(model);
    }

    public static <T> ResponseEntity<ResponseModel<T>> notfound() {
        return error(NOT_FOUND, ErrorCode.ERR404_001, null);
    }
    public static <T> ResponseEntity<ResponseModel<T>> forbidden() {
        return error(FORBIDDEN, ErrorCode.ERR403_001, null);
    }
    public static <T> ResponseEntity<ResponseModel<T>> falsifyToken() {
        return error(UNAUTHORIZED, ErrorCode.ERR401_002, null);
    }
    public static <T> ResponseEntity<ResponseModel<T>> expiredToken() {
        return error(UNAUTHORIZED, ErrorCode.ERR401_001, null);
    }
    public static <T> ResponseEntity<ResponseModel<T>> error() {
        return error(ErrorCode.ERROR, null);
    }
    public static <T> ResponseEntity<ResponseModel<T>> error(final String errorMsg) {
        return error(ErrorCode.ERROR, errorMsg);
    }
    public static <T> ResponseEntity<ResponseModel<T>> error(final ErrorCode errorCode) {
        return error(errorCode, errorCode.getMessage());
    }
    public static <T> ResponseEntity<ResponseModel<T>> systemMessage(final ErrorCode errorCode, final T body) {
        return error(SERVICE_UNAVAILABLE, errorCode.getCode(), errorCode.getMessage(), errorCode.getMessage(), body );
    }
    public static ResponseEntity<?> error(final ErrorField errorField) {
        return error(BAD_REQUEST, ErrorCode.INVALID_PARAM, errorField.getErrors().toString(), errorField);
    }
    public static <T> ResponseEntity<ResponseModel<T>> error(final String errorCode, final String errorMsg) {
        return error(BAD_REQUEST, errorCode, errorMsg, errorMsg, null );
    }
    public static <T> ResponseEntity<ResponseModel<T>> error(final ErrorCode errorCode, final String errorMsg) {
        return error(BAD_REQUEST, errorCode, errorMsg);
    }
    public static <T> ResponseEntity<ResponseModel<T>> serverError() {
        return error(INTERNAL_SERVER_ERROR, ErrorCode.SERVER_ERROR, null);
    }
    public static <T> ResponseEntity<ResponseModel<T>> error(final HttpStatus status, final ErrorCode errorCode) {
        return error(status, errorCode, errorCode.getMessage(), null );
    }
    public static <T> ResponseEntity<ResponseModel<T>> error(final HttpStatus status, final ErrorCode errorCode, final String errorMsg ) {
        return error(status, errorCode, errorMsg, null );
    }
    public static <T> ResponseEntity<ResponseModel<T>> error(final HttpStatus status, final ErrorCode errorCode, final String errorMsg, final T errorField ) {
        return error(status, errorCode.getCode(), errorCode.getMessage(), errorMsg, errorField );
    }
    private static <T> ResponseEntity<ResponseModel<T>> error(final HttpStatus status, final String code, final String message, final String errorMsg, final T errorField ) {
        ResponseModel<T> model = new ResponseModel<T>();

        model.setCode(code);
        model.setMessage(message);
        model.setData(errorField);

        return ResponseEntity
                .status(status)
                .body(model);
    }
    public static <T> ResponseEntity<ResponseModel<T>> fieldError(final T errorField ) {
        return error(BAD_REQUEST, ErrorCode.INVALID_PARAM.getCode(), ErrorCode.INVALID_PARAM.getMessage(), null, errorField );
    }

    public static <B, A> A convert(final B before, final Class<A> after) {
        return objectMapper.convertValue(before, after);
    }
    public static <B, A> List<A> convert(final List<B> before, final Class<A> after) {
        return before.stream().map(item -> convert(item, after)).collect(Collectors.toList());
    }

    public static <T> T jsonStrToClass (String jsonString, Class<T> valueType) {
        try {
            return objectMapper.readValue(jsonString, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public static String classToJsonStr(final Object after) {
        try {
            return objectMapper.writeValueAsString(after);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getClientIp () {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String getServerIp () {
        String hostAddr = "";
        try {
            Enumeration<NetworkInterface> nienum = NetworkInterface.getNetworkInterfaces();
            while (nienum.hasMoreElements()) {
                NetworkInterface ni = nienum.nextElement();
                Enumeration<InetAddress> kk= ni.getInetAddresses();
                while (kk.hasMoreElements()) {
                    InetAddress inetAddress = kk.nextElement();
                    if (!inetAddress.isLoopbackAddress() &&
                            !inetAddress.isLinkLocalAddress() &&
                            inetAddress.isSiteLocalAddress()) {
                        hostAddr = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            printStackTraceInfo(e);
        }
        return hostAddr;
    }

    public static void printStackTraceInfo(final Exception error) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        log.error("====================================================================");
        log.error("Exception \t\t: " + error.getClass().getSimpleName());
        log.error("Request URI \t\t: " + request.getRequestURI());
        log.error("Request Method \t: " + request.getMethod());

        String param = RequestUtil.removeMapInKey(request);
        if (StringUtils.hasText(param)) {
            log.error("Param \t\t\t: " + param);
        }

        String body = RequestUtil.removeJsonObjectInKey(request);
        if (StringUtils.hasText(body)) {
            log.error("Body \t\t\t: " + body);
        }

        if (!ObjectUtils.isEmpty(error.getCause())) {
            log.error("Cause \t\t\t: " + error.getCause());
        }

        if (StringUtils.hasText(error.getMessage())) {
            log.error("Message \t\t\t: " + error.getMessage());
        }

        String accessToken = request.getHeader(HEADER_AUTH);
        if (StringUtils.hasText(HEADER_AUTH) && StringUtils.hasText(accessToken)) {
            log.error("Access Token \t: " + accessToken);
        }

        String refreshToken = request.getHeader(HEADER_AUTH_REFRESH);
        if (StringUtils.hasText(HEADER_AUTH_REFRESH) && StringUtils.hasText(refreshToken)) {
            log.error("Refresh Token \t: " + refreshToken);
        }

        for (StackTraceElement element : error.getStackTrace()) {
            String target = element.toString();
            if (target.contains("com.example.demo") && !target.contains("<generated>")) {
                log.error("at " + element);
            }
        }
        log.error("====================================================================");
    }

    public static CustomErrorCode getEnumByCode(String enumPackage, String enumCode) {
        try {
            // name으로 클래스 로드 (패키지 경로 포함)
            Class<?> enumClass = Class.forName(enumPackage);

            // enum 클래스인지 확인
            if (enumClass.isEnum() && CustomErrorCode.class.isAssignableFrom(enumClass)) {
                // 해당 enum 클래스의 모든 상수 가져오기
                Object[] enumConstants = enumClass.getEnumConstants();

                for (Object enumConstant : enumConstants) {
                    if (((Enum<?>) enumConstant).name().equals(enumCode)) {
                        return (CustomErrorCode) enumConstant; // 인터페이스 타입으로 캐스팅
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            printStackTraceInfo(ex);
        }
        return null;
    }

    public static boolean hasRequest() {
        return !ObjectUtils.isEmpty(RequestContextHolder.getRequestAttributes());
    }

    public static <T> T getAttribute(String attributeName, Class<T> valueType) {
        if (hasRequest()) {
            HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            return (T) req.getAttribute(attributeName);
        } else {
            return null;
        }
    }

    public static <T> T getAttribute(String attributeName) {
        return (T) getAttribute(attributeName, String.class);
    }

    public static <T> T getHeader(String headerName, Class<T> valueType) {
        if (hasRequest()) {
            HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            return (T) req.getHeader(headerName);
        } else {
            return null;
        }
    }

    public static <T> T getHeader(String headerName) {
        return (T) getHeader(headerName, String.class);
    }

}
