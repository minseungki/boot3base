package com.example.demo.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;
import redis.embedded.RedisServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
@Profile("local")
@Configuration
@EnableCaching
public class LocalRedisConfig {

	@Value("${spring.data.redis.host}")
	private String redisHost;

	@Value("${spring.data.redis.port}")
	private int redisPort;

	private boolean isRedisRunning = false;

	private RedisServer redisServer;

	@PostConstruct
	public void redisServer() throws IOException {
		isRedisRunning = isRedisRunning(redisPort);
		if (!isRedisRunning) {
			redisServer = RedisServer.builder()
					.port(redisPort)
					.setting("maxmemory 128M")
					.build();
			redisServer.start();
		}
	}

	@PreDestroy
	public void stopRedis() {
		if (redisServer != null) {
			redisServer.stop();
		}
	}

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(redisHost, redisPort);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory());
		template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
		template.setKeySerializer(new StringRedisSerializer());
		template.setEnableDefaultSerializer(true);
		return template;
	}

	@Bean
	public StringRedisTemplate stringRedisTemplate() {
		StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
		stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
		stringRedisTemplate.setValueSerializer(new StringRedisSerializer());
		stringRedisTemplate.setConnectionFactory(redisConnectionFactory());
		return stringRedisTemplate;
	}

	/**
	 * Embedded Redis가 현재 실행중인지 확인
	 */
	private boolean isRedisRunning(int port) throws IOException {
		if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
			return isRunning(executeFindProcessCommand(redisPort));
		} else {
			return isRunning(executeGrepProcessCommand(redisPort));
		}
	}

	/**
	 * 해당 port를 사용중인 프로세스 확인하는 sh 실행
	 */
	private Process executeGrepProcessCommand(int port) throws IOException {
		String command = String.format("netstat -nat | grep LISTEN|grep %d", port);
		String[] shell = {"/bin/sh", "-c", command};
		return Runtime.getRuntime().exec(shell);
	}

	/**
	 * 해당 port를 사용중인 프로세스 확인하는 command 실행
	 */
	private Process executeFindProcessCommand(int port) throws IOException {
		String command = String.format("netstat -ano | find \"LISTEN\" | find \"%d\"", port);
		String[] shell = {"cmd", "/c", command};
		return Runtime.getRuntime().exec(shell);
	}

	/**
	 * 해당 Process가 현재 실행중인지 확인
	 */
	private boolean isRunning(Process process) throws IOException {
		String line;
		StringBuilder pidInfo = new StringBuilder();

		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		while ((line = input.readLine()) != null) {
			pidInfo.append(line);
		}

		return StringUtils.hasText(pidInfo.toString());
	}
}
