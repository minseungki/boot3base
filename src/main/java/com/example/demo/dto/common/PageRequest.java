package com.example.demo.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
public class PageRequest {

	@NotNull(message = "페이지 번호는 필수값입니다.")
	@Schema(description = "페이지 번호", example = "1")
	int currentPage;

}
