package com.shorturl.serviceimpl;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
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

	private ZoneId getUserZone(String timezone) {
		try {
			return ZoneId.of(timezone);
		} catch (Exception e) {
			return ZoneId.of("UTC");
		}
	}

	@Override
	@Transactional
	public GenericResponseModel<UrlShortenerModel> createShortUrl(UrlShortenerDto requestDto, String timezone) {

		try {

			ZoneId userZone = getUserZone(timezone);

			UrlShortenerEntity existingEntity = repo.findByOriginalUrl(requestDto.getOriginalUrl());

			if (existingEntity != null) {
				UrlShortenerModel existingModel = util.mapToModel(existingEntity, baseUrl, userZone);

				return new GenericResponseModel<>(HttpStatus.OK.value(), HttpStatus.OK, existingModel, null,
						"Short URL already exists for given URL");
			}

			String shortCode;

			if (requestDto.getCustomAlias() != null && !requestDto.getCustomAlias().trim().isEmpty()) {

				shortCode = requestDto.getCustomAlias().trim();

				if (repo.existsByShortCode(shortCode)) {
					return new GenericResponseModel<>(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, null,
							null, "Custom alias already taken");
				}

			} else {
				shortCode = util.generateShortCode();
			}

			UrlShortenerEntity entity = util.mapToEntity(requestDto, shortCode);

			entity = repo.save(entity);

			long ttlSeconds = Duration.between(Instant.now(), entity.getExpiresAt()).getSeconds();

			if (ttlSeconds > 0) {
				redisService.saveUrlMapping(entity.getShortCode(), entity.getOriginalUrl(), ttlSeconds);
			}

			UrlShortenerModel model = util.mapToModel(entity, baseUrl, userZone);

			return new GenericResponseModel<>(HttpStatus.CREATED.value(), HttpStatus.CREATED, model, null,
					"Short URL generated successfully");

		} catch (Exception e) {

			return new GenericResponseModel<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					HttpStatus.INTERNAL_SERVER_ERROR, null, e.getMessage(), "Short URL generation failed");
		}
	}

	@Override
	public GenericResponseModelList<List<UrlShortenerModel>> fetchAllShortUrls(Integer page, Integer size,
			String timezone) {

		ZoneId userZone = getUserZone(timezone);

		Pageable pageable = PageRequest.of(page - 1, size);

		Page<UrlShortenerEntity> entityPage = repo.findAll(pageable);

		List<UrlShortenerModel> modelList = util.mapToModelList(entityPage.getContent(), baseUrl, userZone);

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

		UrlShortenerEntity entity = repo.findByShortCode(shortCode);

		if (entity == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Short URL not found");
			return;
		}

		if (entity.getExpiresAt().isBefore(Instant.now())) {

			redisService.deleteUrlMapping(shortCode);

			response.sendError(HttpServletResponse.SC_GONE, "Short URL expired");
			return;
		}

		String originalUrl;

		String cachedUrl = redisService.getOriginalUrl(shortCode);

		if (cachedUrl != null) {
			originalUrl = cachedUrl;
		} else {
			originalUrl = entity.getOriginalUrl();

			long ttlSeconds = Duration.between(Instant.now(), entity.getExpiresAt()).getSeconds();

			if (ttlSeconds > 0) {
				redisService.saveUrlMapping(shortCode, originalUrl, ttlSeconds);
			}
		}

		repo.incrementClickCount(shortCode);

		response.sendRedirect(originalUrl);
	}

	@Override
	@Scheduled(cron = "0 0 */2 * * *")
	public void autoDelete() {

		List<UrlShortenerEntity> expiredLinks = repo.findExpiredLinks(Instant.now());

		for (UrlShortenerEntity object : expiredLinks) {
			redisService.deleteUrlMapping(object.getShortCode());
		}

		repo.deleteAll(expiredLinks);
	}

	@Override
	public boolean isAliasTaken(String alias) {
		return repo.existsByShortCode(alias);
	}
}