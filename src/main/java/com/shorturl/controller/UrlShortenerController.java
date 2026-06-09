package com.shorturl.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shorturl.dto.UrlShortenerDto;
import com.shorturl.model.GenericResponseModel;
import com.shorturl.model.GenericResponseModelList;
import com.shorturl.model.UrlShortenerModel;
import com.shorturl.service.UrlShortenerService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/compactURL")
public class UrlShortenerController {

	@Autowired
	private UrlShortenerService service;

	@PostMapping("/generate")
	public GenericResponseModel<UrlShortenerModel> createShortUrl(@Valid @RequestBody UrlShortenerDto requestDto,
			@RequestHeader(value = "X-Timezone", required = false, defaultValue = "UTC") String timezone) {

		return service.createShortUrl(requestDto, timezone);
	}

	@GetMapping("/fetchAll")
	public GenericResponseModelList<List<UrlShortenerModel>> fetchAllShortUrls(
			@RequestParam(defaultValue = "1") @Min(1) Integer page,
			@RequestParam(defaultValue = "10") @Min(10) Integer size,
			@RequestHeader(value = "X-Timezone", required = false, defaultValue = "UTC") String timezone) {

		return service.fetchAllShortUrls(page, size, timezone);
	}

	@GetMapping("/{shortCode}")
	public void redirectUrl(@PathVariable String shortCode, HttpServletResponse response) throws IOException {

		service.redirectUrl(shortCode, response);
	}

	@GetMapping("/check-alias/{alias}")
	public GenericResponseModel<Boolean> checkAlias(@PathVariable String alias) {
		boolean exists = service.isAliasTaken(alias);
		return new GenericResponseModel<>(200, org.springframework.http.HttpStatus.OK, exists, null,
				exists ? "Alias already taken" : "Alias available");
	}
}