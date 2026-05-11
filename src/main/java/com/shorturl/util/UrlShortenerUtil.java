package com.shorturl.util;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.shorturl.dto.UrlShortenerDto;
import com.shorturl.entity.UrlShortenerEntity;
import com.shorturl.model.UrlShortenerModel;
import com.shorturl.repository.UrlShortenerRepo;

@Component
public class UrlShortenerUtil {

	@Autowired
	private UrlShortenerRepo repo;

	private static final Random RANDOM = new Random();

	public String generateShortCode() {

		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

		String code;

		do {
			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < 6; i++) {
				sb.append(characters.charAt(RANDOM.nextInt(characters.length())));
			}

			code = sb.toString();

		} while (repo.existsByShortUrl("http://localhost:8080/" + code));

		return code;
	}

	public UrlShortenerEntity mapToEntity(UrlShortenerDto dto, String shortUrl) {

		UrlShortenerEntity entity = new UrlShortenerEntity();

		if (dto.getOriginalUrl() != null && !dto.getOriginalUrl().isBlank()) {
			entity.setOriginalUrl(dto.getOriginalUrl());
		}

		if (shortUrl != null && !shortUrl.isBlank()) {
			entity.setShortUrl(shortUrl);
		}

		return entity;
	}

	public UrlShortenerModel mapToModel(UrlShortenerEntity entity) {

		UrlShortenerModel model = new UrlShortenerModel();

		if (entity.getId() != null) {
			model.setId(entity.getId());
		}

		if (entity.getOriginalUrl() != null && !entity.getOriginalUrl().isBlank()) {
			model.setOriginalUrl(entity.getOriginalUrl());
		}

		if (entity.getShortUrl() != null && !entity.getShortUrl().isBlank()) {
			model.setShortUrl(entity.getShortUrl());
		}

		if (entity.getCreatedAt() != null) {
			model.setCreatedAt(entity.getCreatedAt());
		}
		
		if (entity.getExpiresAt() != null) {
			model.setExpiryAt(entity.getExpiresAt());
		}

		if (entity.getClickCount() != null) {
			model.setClickCount(entity.getClickCount());
		}

		return model;
	}

	public List<UrlShortenerModel> mapToModelList(List<UrlShortenerEntity> entityList) {

		return entityList.stream().map(this::mapToModel).collect(Collectors.toList());
	}
}