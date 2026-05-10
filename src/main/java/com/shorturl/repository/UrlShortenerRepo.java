package com.shorturl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.shorturl.entity.UrlShortenerEntity;

public interface UrlShortenerRepo extends JpaRepository<UrlShortenerEntity, Long> {

    boolean existsByShortUrl(String shortUrl);

    UrlShortenerEntity findByShortUrl(String shortUrl);
}