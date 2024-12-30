package com.example.demo.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class DateRequest extends PageRequest {

	// 파라미터 용
	@DateTimeFormat(pattern = SwaggerSample.LOCAL_DATE_FORMAT)
	@Schema(description = "조회_시작일", example = SwaggerSample.LOCAL_DATE_SAMPLE_ST)
	private LocalDate beginDt;

	@Schema(description = "조회_종료일", example = SwaggerSample.LOCAL_DATE_SAMPLE_END)
	private LocalDate endDt;

	// 쿼리용
	@Schema(hidden = true)
	@JsonIgnore
	private LocalDateTime queryBeginDt;

	@Schema(hidden = true)
	@JsonIgnore
	private LocalDateTime queryEndDt;

}
