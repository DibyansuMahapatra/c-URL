package com.shorturl.service;

import java.io.IOException;
import java.util.List;

import com.shorturl.dto.UrlShortenerDto;
import com.shorturl.model.GenericResponseModel;
import com.shorturl.model.GenericResponseModelList;
import com.shorturl.model.UrlShortenerModel;

import jakarta.servlet.http.HttpServletResponse;

public interface UrlShortenerService {

	/**
	 * Creates a short URL for a given original URL
	 *
	 * @param requestDto - Dto Object that brings the long/original URL
	 * @return GenericResponseModel containing the saved UrlShortenerEntity
	 */
	GenericResponseModel<UrlShortenerModel> createShortUrl(UrlShortenerDto requestDto);

	GenericResponseModelList<List<UrlShortenerModel>> fetchAllShortUrls(Integer page, Integer size);

	void redirectUrl(UrlShortenerDto requestUrl, HttpServletResponse response) throws IOException;

	void autoDelete();
}