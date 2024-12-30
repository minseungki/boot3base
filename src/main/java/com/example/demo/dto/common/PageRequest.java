package com.example.demo.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class PageRequest {

	@Schema(description = "페이지 번호", example = "1")
	int currentPage;

}
