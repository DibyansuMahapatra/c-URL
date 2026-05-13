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
	
	/**
	 * Returns a list of shortUrl -> originalUrl Objects
	 *
	 * @param page, size- Integer variables that set the size of page
	 * @return A list of GenericResponseModelList type, containing all the saved UrlShortenerEntity
	 */
	GenericResponseModelList<List<UrlShortenerModel>> fetchAllShortUrls(Integer page, Integer size);

	/**
	 * Redirects a short URL to it's corresponding original URL
	 *
	 * @param shortCode - String variable that is required to redirect to the original URL
	 * @return No return, as link redirects
	 */
	void redirectUrl(String shortCode, HttpServletResponse response) throws IOException;
	
	/**
	 * Deletes expired short URL -> original URL Objects automatically
	 *
	 * @param No parameter / parameters
	 * @return No return, as it is a scheduled DB cleaner
	 */
	void autoDelete();
}