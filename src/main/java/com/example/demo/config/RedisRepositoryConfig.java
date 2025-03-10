package com.example.demo.config;

import io.lettuce.core.ClientOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@EnableCaching
@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
@Profile("!local")
public class RedisRepositoryConfig extends CachingConfigurerSupport {

	@Value("${spring.data.redis.host}")
	private String redisHost;

	@Value("${spring.data.redis.port}")
	private int redisPort;

	@Value("${spring.profiles.active}")
	private String profile;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
				.clientOptions(
						ClientOptions.builder()
								.autoReconnect(true)
								.pingBeforeActivateConnection(true)
								.build()
				).build();

		if ("prod".equals(profile)) {
			RedisClusterConfiguration redisConfig = new RedisClusterConfiguration();
			redisConfig.clusterNode(redisHost, redisPort);
			return new LettuceConnectionFactory(redisConfig, clientConfig);
		} else {
			RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
			redisConfig.setHostName(redisHost);
			redisConfig.setPort(redisPort);
			return new LettuceConnectionFactory(redisConfig, clientConfig);
		}
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

}
