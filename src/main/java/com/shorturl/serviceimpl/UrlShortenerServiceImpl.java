package com.shorturl.serviceimpl;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shorturl.dto.UrlShortenerDto;
import com.shorturl.entity.UrlShortenerEntity;
import com.shorturl.model.GenericResponseModel;
import com.shorturl.model.GenericResponseModelList;
import com.shorturl.model.UrlShortenerModel;
import com.shorturl.repository.UrlShortenerRepo;
import com.shorturl.service.RedisService;
import com.shorturl.service.UrlShortenerService;
import com.shorturl.util.UrlShortenerUtil;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class UrlShortenerServiceImpl implements UrlShortenerService {

	@Autowired
	private UrlShortenerRepo repo;

	@Autowired
	private UrlShortenerUtil util;

	@Autowired
	private RedisService redisService;

	@Value("${app.base-url}")
	private String baseUrl;

	@Override
	@Transactional
	public GenericResponseModel<UrlShortenerModel> createShortUrl(UrlShortenerDto requestDto) {

		try {

			// Check if original URL already exists
			UrlShortenerEntity existingEntity = repo.findByOriginalUrl(requestDto.getOriginalUrl());

			if (existingEntity != null) {

				UrlShortenerModel existingModel = util.mapToModel(existingEntity, baseUrl);

				return new GenericResponseModel<>(HttpStatus.OK.value(), HttpStatus.OK, existingModel, null,
						"Short URL already exists for given URL");
			}

			/********** Flow for generating new URL **********/

			// Generate unique short code
			String shortCode = util.generateShortCode();

			// Map DTO -> Entity
			UrlShortenerEntity entity = util.mapToEntity(requestDto, shortCode);

			// Save in DB
			entity = repo.save(entity);

			// Save in Redis
			long ttlMinutes = Math.max(1, Duration.between(LocalDateTime.now(), entity.getExpiresAt()).toMinutes());
			redisService.saveUrlMapping(entity.getShortCode(), entity.getOriginalUrl(), ttlMinutes);

			// Entity -> Model
			UrlShortenerModel model = util.mapToModel(entity, baseUrl);

			return new GenericResponseModel<>(HttpStatus.CREATED.value(), HttpStatus.CREATED, model, null,
					"Short URL generated successfully");

		} catch (Exception e) {

			return new GenericResponseModel<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					HttpStatus.INTERNAL_SERVER_ERROR, null, e.getMessage(), "Short URL generation failed");
		}
	}

	@Override
	public GenericResponseModelList<List<UrlShortenerModel>> fetchAllShortUrls(Integer page, Integer size) {

		Pageable pageable = PageRequest.of(page - 1, size);

		Page<UrlShortenerEntity> entityPage = repo.findAll(pageable);

		List<UrlShortenerModel> modelList = util.mapToModelList(entityPage.getContent(), baseUrl);

		GenericResponseModelList<List<UrlShortenerModel>> response = new GenericResponseModelList<>();

		response.setStatusCode(HttpStatus.OK.value());
		response.setStatus(HttpStatus.OK);
		response.setData(modelList);
		response.setMessage(modelList.isEmpty() ? "No URLs found" : "URLs fetched successfully");

		response.setTotalElements(entityPage.getTotalElements());
		response.setPage((long) entityPage.getNumber() + 1);
		response.setSize((long) entityPage.getSize());

		return response;
	}

	@Override
	public void redirectUrl(String shortCode, HttpServletResponse response) throws IOException {

		if (shortCode == null || shortCode.isBlank()) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid short code");

			return;
		}

		// 1. Check Redis
		String originalUrl = redisService.getOriginalUrl(shortCode);

		// CACHE HIT
		if (originalUrl != null) {

			UrlShortenerEntity entity = repo.findByShortCode(shortCode);

			if (entity == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			if (entity.getExpiresAt().isBefore(LocalDateTime.now())) {

				redisService.deleteUrlMapping(shortCode);

				response.sendError(HttpServletResponse.SC_GONE, "Short URL expired");

				return;
			}

			repo.incrementClickCount(shortCode);

			response.sendRedirect(originalUrl);
			return;
		}

		// CACHE MISS
		UrlShortenerEntity entity = repo.findByShortCode(shortCode);

		if (entity == null) {

			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Short URL not found");

			return;
		}

		// Check Expiry
		if (entity.getExpiresAt().isBefore(LocalDateTime.now())) {

			response.sendError(HttpServletResponse.SC_GONE, "Short URL expired");

			return;
		}

		// Save to Redis
		long ttlMinutes = Math.max(1, Duration.between(LocalDateTime.now(), entity.getExpiresAt()).toMinutes());

		redisService.saveUrlMapping(entity.getShortCode(), entity.getOriginalUrl(), ttlMinutes);

		// Increment Click Count
		repo.incrementClickCount(shortCode);

		response.sendRedirect(entity.getOriginalUrl());
	}

	@Override
	@Scheduled(cron = "0 */30 * * * *")
	public void autoDelete() {

		// Get expired links first
		List<UrlShortenerEntity> expiredLinks = repo.findExpiredLinks(LocalDateTime.now());

		// Delete from Redis
		for (UrlShortenerEntity object : expiredLinks) {
			redisService.deleteUrlMapping(object.getShortCode());
		}

		// Delete from DB
		repo.deleteAll(expiredLinks);
	}
}