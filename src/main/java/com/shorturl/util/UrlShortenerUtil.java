package com.shorturl.util;

import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.shorturl.dto.UrlShortenerDto;
import com.shorturl.entity.UrlShortenerEntity;
import com.shorturl.model.UrlShortenerModel;
import com.shorturl.repository.UrlShortenerRepo;

@Component
public class UrlShortenerUtil {

	@Autowired
	private UrlShortenerRepo repo;

	@Value("${app.base-url}")
	private String baseUrl;

	private static final Random RANDOM = new Random();

	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	private static final int SHORT_CODE_LENGTH = 10;

	public String generateShortCode() {

		String shortCode;

		do {

			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
				sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
			}

			shortCode = sb.toString();

		} while (repo.existsByShortCode(shortCode));

		return shortCode;
	}

	public UrlShortenerEntity mapToEntity(UrlShortenerDto dto, String shortCode) {

		UrlShortenerEntity entity = new UrlShortenerEntity();

		if (dto.getOriginalUrl() != null && !dto.getOriginalUrl().isBlank()) {
			entity.setOriginalUrl(dto.getOriginalUrl());
		}

		if (shortCode != null && !shortCode.isBlank()) {
			entity.setShortCode(shortCode);
		}

		return entity;
	}

	public UrlShortenerModel mapToModel(UrlShortenerEntity entity, String baseUrl, ZoneId userZone) {

		UrlShortenerModel model = new UrlShortenerModel();

		if (entity.getId() != null) {
			model.setId(entity.getId());
		}

		if (entity.getOriginalUrl() != null && !entity.getOriginalUrl().isBlank()) {
			model.setOriginalUrl(entity.getOriginalUrl());
		}

		if (entity.getShortCode() != null && !entity.getShortCode().isBlank()) {
			StringBuilder shortUrl = new StringBuilder(baseUrl);
			shortUrl.append(entity.getShortCode());
			model.setShortUrl(shortUrl.toString());
		}

		if (entity.getCreatedAt() != null) {
			model.setCreatedAt(entity.getCreatedAt().atZone(userZone));
		}

		if (entity.getExpiresAt() != null) {
			model.setExpiryAt(entity.getExpiresAt().atZone(userZone));
		}

		if (entity.getClickCount() != null) {
			model.setClickCount(entity.getClickCount());
		}

		return model;
	}

	public List<UrlShortenerModel> mapToModelList(List<UrlShortenerEntity> entityList, String baseUrl,
			ZoneId userZone) {

		return entityList.stream().map(entity -> mapToModel(entity, baseUrl, userZone)).collect(Collectors.toList());
	}
}