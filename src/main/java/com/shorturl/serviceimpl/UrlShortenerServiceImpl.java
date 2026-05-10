package com.shorturl.serviceimpl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.shorturl.dto.UrlShortenerDto;
import com.shorturl.entity.UrlShortenerEntity;
import com.shorturl.model.GenericResponseModel;
import com.shorturl.model.GenericResponseModelList;
import com.shorturl.model.UrlShortenerModel;
import com.shorturl.repository.UrlShortenerRepo;
import com.shorturl.service.UrlShortenerService;
import com.shorturl.util.UrlShortenerUtil;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class UrlShortenerServiceImpl implements UrlShortenerService {

	@Autowired
	private UrlShortenerRepo repo;

	@Autowired
	private UrlShortenerUtil util;

	@Override
	public GenericResponseModel<UrlShortenerModel> createShortUrl(UrlShortenerDto requestDto) {

		try {

			// Check if already exists
			if (repo.existsByOriginalUrl(requestDto.getOriginalUrl())) {
				return new GenericResponseModel<>(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT, null,
						"Given URL's Short URL already exists", null);
			}

			// Generate a unique short URL code
			String baseUrl = "http://localhost:8080/";
			String shortCode = util.generateShortCode();
			String shortUrl = baseUrl + shortCode;

			// Map from Dto to Entity Object Safely
			UrlShortenerEntity entity = util.mapToEntity(requestDto, shortUrl);

			// Save Entity
			entity = repo.save(entity);

			// Map from Entity to Model to return Response Safely
			UrlShortenerModel model = util.mapToModel(entity);

			return new GenericResponseModel<>(HttpStatus.CREATED.value(), HttpStatus.CREATED, model, null,
					"Short URL Generated Successfully");

		} catch (Exception e) {
			return new GenericResponseModel<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					HttpStatus.INTERNAL_SERVER_ERROR, null, e.getMessage(), "No URL passed to generate Short URL");
		}
	}

	@Override
	public GenericResponseModelList<List<UrlShortenerModel>> fetchAllShortUrls(UrlShortenerDto paginationRequest) {

		Integer page = paginationRequest.getPage();
		Integer size = paginationRequest.getSize();
		if (page == null || size == null) {
			page = 1;
			size = 10;
		}

		Pageable pageable = PageRequest.of(page - 1, size);

		Page<UrlShortenerEntity> entityPage = repo.findAll(pageable);

		List<UrlShortenerModel> modelList = util.mapToModelList(entityPage.getContent());

		GenericResponseModelList<List<UrlShortenerModel>> response = new GenericResponseModelList<>();

		response.setStatusCode(HttpStatus.OK.value());
		response.setStatus(HttpStatus.OK);
		response.setData(modelList);
		response.setMessage("Links Fetched Successfully");
		// Page Meta Data
		response.setTotalElements(entityPage.getTotalElements());
		response.setPage((long) entityPage.getNumber() + 1);
		response.setSize((long) entityPage.getSize());

		return response;
	}

	@Override
	public void redirectUrl(UrlShortenerDto requestUrl, HttpServletResponse response) throws IOException {

		UrlShortenerEntity entity = repo.findByShortUrl(requestUrl.getShortUrl());

		if (entity == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		entity.setClickCount(entity.getClickCount() + 1);
		repo.save(entity);

		response.sendRedirect(entity.getOriginalUrl());
	}
}