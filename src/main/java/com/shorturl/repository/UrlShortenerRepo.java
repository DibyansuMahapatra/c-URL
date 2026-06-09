package com.shorturl.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shorturl.entity.UrlShortenerEntity;

import jakarta.transaction.Transactional;

@Repository
public interface UrlShortenerRepo extends JpaRepository<UrlShortenerEntity, Long> {

	boolean existsByShortCode(String shortCode);

	UrlShortenerEntity findByShortCode(String shortCode);

	boolean existsByOriginalUrl(String originalUrl);

	UrlShortenerEntity findByOriginalUrl(String originalUrl);

	@Query("SELECT s FROM UrlShortenerEntity s WHERE s.expiresAt <= :now")
	List<UrlShortenerEntity> findExpiredLinks(@Param("now") Instant now);

	@Modifying
	@Transactional
	@Query("UPDATE UrlShortenerEntity u SET u.clickCount = u.clickCount + 1 WHERE u.shortCode = :shortCode")
	void incrementClickCount(@Param("shortCode") String shortCode);
}