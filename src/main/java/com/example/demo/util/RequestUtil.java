package com.example.demo.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RequestUtil {

	private static final List<String> REMOVE_PARAM_KEY_LIST = Arrays.asList("password", "nowPassword", "newPassword", "mobilePhoneNo", "name", "id", "email");

	public static String removeJsonObjectInKey(HttpServletRequest req) {
		JSONObject obj = null;
		try {
			String param = IOUtils.toString(req.getReader());
			if (!StringUtils.hasText(param)) {
				return null;
			}

			JSONParser jsonParser = new JSONParser();
			obj = (JSONObject) jsonParser.parse(param);

			for (String key : REMOVE_PARAM_KEY_LIST) {
				boolean existKey = Optional.ofNullable(obj.get(key)).isPresent();
				if (existKey) {
					obj.put(key, "***");
//					obj.remove(key);
				}
			}
		} catch (ParseException | IOException | ClassCastException ex) {
			log.error("body parse error");
		}

		if (ObjectUtils.isEmpty(obj)) {
			return null;
		} else {
			return obj.toString();
		}
	}

	public static String removeMapInKey(HttpServletRequest req){
		Map<String, String[]> paramMap = req.getParameterMap();
		Set<String> paramKeys = paramMap.keySet();
		Map<String, String[]> resultMap = new HashMap<>();

		for (String key : paramKeys) {
			String[] values = paramMap.get(key);
			for (String value : values) {
				if(REMOVE_PARAM_KEY_LIST.contains(key)) {
					values = new String[]{value.replace(value, "")};
				}
			}
			resultMap.put(key, values);
		}

		return paramMapToString(resultMap);
	}

	private static String paramMapToString(Map<String, String[]> paraStringMap) {
		return paraStringMap.entrySet().stream()
				.map(entry -> String.format("%s : %s",
						entry.getKey(), Arrays.toString(entry.getValue())))
				.collect(Collectors.joining(", "));
	}

}
