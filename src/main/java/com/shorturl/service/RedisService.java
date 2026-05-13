package com.shorturl.service;

public interface RedisService {

	void saveUrlMapping(String shortUrl, String originalUrl, Long expiryMinutes);

	String getOriginalUrl(String shortUrl);

	void deleteUrlMapping(String shortUrl);
}