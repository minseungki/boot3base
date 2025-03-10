package com.example.demo.dto.common;

import lombok.*;

@Getter @Setter
public class RedisCacheKey {

	public static final String BBS = "cache:bbs";
	public static final String BBS_DEL = BBS + "*";
	public static final String FILE = "cache:file";
	public static final String FILE_DEL = FILE + "*";

}
