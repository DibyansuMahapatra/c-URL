package com.shorturl.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.shorturl.dto.UrlShortenerDto;
import com.shorturl.entity.UrlShortenerEntity;
import com.shorturl.model.GenericResponseModel;
import com.shorturl.model.UrlShortenerModel;
import com.shorturl.repository.UrlShortenerRepo;
import com.shorturl.service.UrlShortenerService;
import com.shorturl.util.UrlShortenerUtil;

@Service
public class UrlShortenerServiceImpl implements UrlShortenerService {

	@Autowired
	private UrlShortenerRepo repo;

	@Autowired
	private UrlShortenerUtil util;

	@Override
	public GenericResponseModel<UrlShortenerModel> createShortUrl(UrlShortenerDto requestDto) {

		try {
			// Generate a unique short URL code
			String shortCode = util.generateShortCode();
			String shortUrl = "http://localhost:8080/" + shortCode;

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
					HttpStatus.INTERNAL_SERVER_ERROR, null, e.getMessage(), "Issue while generating URL");
		}
	}

	@Override
	public GenericResponseModel<List<UrlShortenerModel>> fetchAllShortUrls() {

		List<UrlShortenerEntity> entityList = repo.findAll();

		// Map all Entity objects to Model objects
		List<UrlShortenerModel> model = util.mapToModelList(entityList);

		return new GenericResponseModel<>(HttpStatus.OK.value(), HttpStatus.OK, model, null,
				"Links Fetched Successfully");
	}
}