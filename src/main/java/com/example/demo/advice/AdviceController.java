package com.example.demo.advice;

import com.example.demo.exception.CustomException;
import com.example.demo.dto.common.enumeration.ErrorField;
import com.example.demo.util.RestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class AdviceController {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<?> methodArgumentNotValidException(final MethodArgumentNotValidException ex) {
        printStackTraceInfo(ex);
        return RestUtil.error(ErrorField.of(ex.getBindingResult()));
    }

    @ExceptionHandler(value = {BindException.class})
    public ResponseEntity<?> bindException(final BindingResult bindingResult) {
        return RestUtil.error(ErrorField.of(bindingResult));
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<?> serverException(final Exception ex) {
        printStackTraceInfo(ex);
        return RestUtil.serverError();
    }

    @ExceptionHandler(value = {CustomException.class})
    public ResponseEntity<?> customException(final CustomException ex) {
        printStackTraceInfo(ex);
        return RestUtil.error(ex.getCode(), ex.getMessage());
    }

//    @ExceptionHandler(value = {JwtException.class})
//    public ResponseEntity<?> jwtException(final JwtException ex) {
//        printStackTraceInfo(ex);
//        return RestUtil.falsifyToken();
//    }


//    @ExceptionHandler(value = {ExpiredJwtException.class})
//    public ResponseEntity<?> expiredJwtException(final ExpiredJwtException ex) {
//        printStackTraceInfo(ex);
//        return RestUtil.expiredToken();
//    }

    private void printStackTraceInfo(final Exception error) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        log.error("====================================================================");
        log.error("[ERROR] Exception \t: " + error.getClass().getSimpleName());
        log.error("[ERROR] Request URI \t: " + request.getRequestURI());
        log.error("[ERROR] Request Method \t: " + request.getMethod());
//        log.error("[ERROR] Param \t: " + RequestUtil.removeMapInKey(request));
//        String body = RequestUtil.removeJsonObjectInKey(request);
//        if (StringUtils.hasText(body)) {
//            log.error("[ERROR] Body \t: " + body);
//        }
        log.error("[ERROR] Cause \t\t: " + error.getCause());
        log.error("[ERROR] Message \t\t: " + error.getMessage());


        for (StackTraceElement element : error.getStackTrace()) {
            String target = element.toString();
            if (target.contains("com.example.demo")) {
                log.error("[ERROR] at " + element);
            }
        }
        log.error("====================================================================");
    }

}