package com.example.demo.advice;

import com.example.demo.exception.CustomException;
import com.example.demo.dto.common.enumeration.ErrorField;
import com.example.demo.util.RestUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class AdviceController {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<?> methodArgumentNotValidException(final MethodArgumentNotValidException ex) {
        RestUtil.printStackTraceInfo(ex);
        return RestUtil.error(ErrorField.of(ex.getBindingResult()));
    }

    @ExceptionHandler(value = {BindException.class})
    public ResponseEntity<?> bindException(final BindingResult bindingResult) {
        return RestUtil.error(ErrorField.of(bindingResult));
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<?> serverException(final Exception ex) {
        RestUtil.printStackTraceInfo(ex);
        return RestUtil.serverError();
    }

    @ExceptionHandler(value = {CustomException.class})
    public ResponseEntity<?> customException(final CustomException ex) {
        RestUtil.printStackTraceInfo(ex);
        return RestUtil.error(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(value = {JwtException.class, UnsupportedJwtException.class})
    public ResponseEntity<?> jwtFalsifyException(final JwtException ex) {
        RestUtil.printStackTraceInfo(ex);
        return RestUtil.falsifyToken();
    }

    @ExceptionHandler(value = {ExpiredJwtException.class})
    public ResponseEntity<?> expiredJwtException(final ExpiredJwtException ex) {
        RestUtil.printStackTraceInfo(ex);
        return RestUtil.expiredToken();
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<?> accessDeniedException(final AccessDeniedException ex) {
        RestUtil.printStackTraceInfo(ex);
        return RestUtil.forbidden();
    }



}