package com.shorturl.serviceimpl;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.shorturl.service.RedisService;

@Service
public class RedisServiceImpl implements RedisService {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public void saveUrlMapping(String shortUrl, String originalUrl, Long expiryMinutes) {

		redisTemplate.opsForValue().set(shortUrl, originalUrl, Duration.ofMinutes(expiryMinutes));
	}

	@Override
	public String getOriginalUrl(String shortUrl) {

		Object value = redisTemplate.opsForValue().get(shortUrl);

		return value != null ? value.toString() : null;
	}

	@Override
	public void deleteUrlMapping(String shortUrl) {

		redisTemplate.delete(shortUrl);
	}
}