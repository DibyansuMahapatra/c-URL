package com.shorturl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shorturl.entity.UrlShortenerEntity;

@Repository
public interface UrlShortenerRepo extends JpaRepository<UrlShortenerEntity, Long> {

	boolean existsByShortUrl(String shortUrl);

	UrlShortenerEntity findByShortUrl(String shortUrl);

	boolean existsByOriginalUrl(String originalUrl);
}