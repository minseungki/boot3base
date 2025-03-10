package com.example.demo.annotation;

import com.example.demo.dto.common.SwaggerSample;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Schema(description = "시퀀스", example = SwaggerSample.SEQUENCE_SAMPLE)
public @interface SeqParamSchema { }
