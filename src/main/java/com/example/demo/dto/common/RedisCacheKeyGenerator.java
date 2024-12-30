package com.example.demo.dto.common;

import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RedisCacheKeyGenerator implements KeyGenerator {

	private final String DELIMITER = "_";

	@Override
	public Object generate(Object target, Method method, Object... params) {

		StringBuffer sbParam = new StringBuffer();
		sbParam.append("(");

		/**
		 * ref. https://velog.io/@hellonewtry/%EC%9E%90%EB%B0%94-%EB%A6%AC%ED%94%8C%EB%A0%89%EC%85%98%EC%9C%BC%EB%A1%9C-%EC%83%81%EC%86%8D%EB%B0%9B%EC%9D%80-field-%EA%B0%92-%EA%B5%AC%ED%95%98%EA%B8%B0
		 */
		for(Object obj : params){
			List<Field> fields = getAllFields(obj);

			for(Field field : fields){
				if(sbParam.length() > 1) {
					sbParam.append(",");
				}
				sbParam.append(field.getName() + "=" + getFieldValue(obj, field.getName()));
			}
		}
		sbParam.append(")");

		String sbKey = target.getClass().getName() + "." +
				method.getName() +
				sbParam;
		return sbKey;
	}

	/**
	 * 전체 필드 목록 얻기
	 * @param t
	 * @param <T>
	 * @return
	 */
	private <T> List<Field> getAllFields(T t){
		Objects.requireNonNull(t);

		Class<?> clazz = t.getClass();
		List<Field> fields = new ArrayList<>();
		while(clazz != null){	// 1. 상위 클래스가 null 이 아닐때까지 모든 필드를 list 에 담는다.
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
			clazz = clazz.getSuperclass();
		}
		return fields;
	}

	/**
	 * 필드명이 일치하는 필드 조회
	 * @param t
	 * @param fieldName
	 * @param <T>
	 * @return
	 */
	private <T> Field getFieldByName(T t, String fieldName){
		Objects.requireNonNull(t);

		Field field = null;
		for(Field f : getAllFields(t)){
			if (f.getName().equals(fieldName)){
				field = f;	// 2. 모든 필드들로부터 fieldName이 일치하는 필드 추출
				break;
			}
		}
		if (field != null){
			field.setAccessible(true);	// 3. 접근 제어자가 private 일 경우
		}
		return field;
	}

	/**
	 * 필드의 값 얻기
	 * @param obj
	 * @param fieldName
	 * @param <T>
	 * @return
	 */
	private <T> T getFieldValue(Object obj, String fieldName){
		Objects.requireNonNull(obj);

		try {
			Field field = getFieldByName(obj, fieldName); // 4. 해당 필드 조회 후
			return (T) field.get(obj);	// 5. get 을 이용하여 field value 획득
		} catch (IllegalAccessException e){
			return null;
		}
	}

}