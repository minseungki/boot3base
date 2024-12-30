package com.example.demo.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class BaseRequest {

	@Schema(description = "등록자_시퀀스", hidden = true)
	private Long registerSeq;

	@Schema(description = "등록_일시", hidden = true)
	private LocalDateTime registrationDt;

	@Schema(description = "등록자_IP", hidden = true)
	private String registerIp;

	@Schema(description = "수정자_시퀀스", hidden = true)
	private Long updaterSeq;

	@Schema(description = "수정_일시", hidden = true)
	private LocalDateTime updateDt;

	@Schema(description = "수정자_IP", hidden = true)
	private String updaterIp;

}
