package com.example.demo.dto.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class RedisCacheKey {

	public static final String FILE = "cache:file";
	public static final String FILE_DEL = FILE+"*";

}
