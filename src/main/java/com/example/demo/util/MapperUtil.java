package com.example.demo.util;

import com.example.demo.dto.common.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.Alias;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@Alias("MapperUtil")
public class MapperUtil {

	public static void setBaseRequest(final BaseRequest dto) {
		Long loginUserSeq = LoginUtil.getLoginUserSeq();
		if (null == loginUserSeq) {
			loginUserSeq = 0L;
		}
		LocalDateTime now = LocalDateTime.now();
		String ip = RestUtil.getClientIp();

		dto.setRegisterSeq(loginUserSeq);
		dto.setRegistrationDt(now);
		dto.setUpdaterSeq(loginUserSeq);
		dto.setUpdateDt(now);
		dto.setRegisterIp(ip);
		dto.setUpdaterIp(ip);
	}

	// 2023-01-01 ~ 2023-01-31
	// 2023-01-01 00:00:00 ~ 2023-01-31 23:59:59

	public static void setBatchBaseRequest(final BaseRequest dto){
		LocalDateTime now = LocalDateTime.now();
		String ip = RestUtil.getServerIp();

		dto.setRegisterSeq(0L);
		dto.setRegistrationDt(now);
		dto.setUpdaterSeq(0L);
		dto.setUpdateDt(now);
		dto.setRegisterIp(ip);
		dto.setUpdaterIp(ip);
	}


	public static void setDateRequest(final DateRequest dto) {
		if (null != dto.getBeginDt() && null != dto.getEndDt()) {
			dto.setQueryBeginDt(parseLocalDateToLocalDateTime(dto.getBeginDt(), LocalTime.MIN));
			dto.setQueryEndDt(parseLocalDateToLocalDateTime(dto.getEndDt(), LocalTime.MAX));
		}
	}

	public static void setBeginDateRequest(final DateRequest dto) {
		if (null != dto.getBeginDt()) {
			dto.setQueryBeginDt(parseLocalDateToLocalDateTime(dto.getBeginDt(), LocalTime.MIN));
		}
	}

	public static void setEndDateRequest(final DateRequest dto) {
		if (null != dto.getEndDt()) {
			dto.setQueryEndDt(parseLocalDateToLocalDateTime(dto.getEndDt(), LocalTime.MAX));
		}
	}

	public static LocalDateTime parseLocalDateToLocalDateTime(final LocalDate localDate, final LocalTime time) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern(SwaggerSample.LOCAL_DATE_TIME_FORMAT);
		return LocalDateTime.parse(of(localDate, time).format(format), format);
	}

	public static String localDateTimeToFormatString(final LocalDateTime localDateTime) {
		if (null != localDateTime) {
			String dateString = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			return dateString.replace(" ", "T");
		} else {
			return null;
		}
	}

	public static String localDateToFormatString(final LocalDate localDate) {
		if (null != localDate) {
			return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		} else {
			return null;
		}
	}

	private static LocalDateTime of(final LocalDate localDate, final LocalTime time) {
		return LocalDateTime.of(localDate, localDate.atTime(time).toLocalTime());
	}

	public static boolean isEmpty(Object obj) {
		return ObjectUtils.isEmpty(obj);
	}

	public static boolean isNotEmpty(Object obj) {
		return !isEmpty(obj);
	}

}
