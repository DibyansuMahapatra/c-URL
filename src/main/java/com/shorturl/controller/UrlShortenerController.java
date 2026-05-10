package com.shorturl.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shorturl.dto.UrlShortenerDto;
import com.shorturl.model.GenericResponseModel;
import com.shorturl.model.GenericResponseModelList;
import com.shorturl.model.UrlShortenerModel;
import com.shorturl.service.UrlShortenerService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/compactURL")
public class UrlShortenerController {

	@Autowired
	private UrlShortenerService service;

	@PostMapping("/generate")
	public GenericResponseModel<UrlShortenerModel> createShortUrl(@RequestBody UrlShortenerDto requestDto) {
		return service.createShortUrl(requestDto);
	}

	@GetMapping("/fetchAll")
	public GenericResponseModelList<List<UrlShortenerModel>> fetchAllShortUrls(
			@RequestBody UrlShortenerDto paginationRequest) {
		return service.fetchAllShortUrls(paginationRequest);
	}

	@GetMapping("/redirect")
	public void redirectUrl(@RequestBody UrlShortenerDto requestUrl, HttpServletResponse response) throws IOException {
		service.redirectUrl(requestUrl, response);
	}

//	@DeleteMapping("/delete")
//	public GenericResponseModel<UrlShortenerEntity> deleteShortUrl() {
//
//		return new GenericResponseModel<>(HttpStatus.NO_CONTENT.value(), HttpStatus.NO_CONTENT, model, null,
//				"URL Deleted Successfully");
//	}
}