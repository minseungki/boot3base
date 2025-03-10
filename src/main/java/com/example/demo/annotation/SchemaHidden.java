package com.example.demo.annotation;

import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Schema(hidden = true)
public @interface SchemaHidden {
}
