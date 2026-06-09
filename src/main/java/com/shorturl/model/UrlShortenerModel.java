package com.shorturl.model;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder({ "id", "originalUrl", "shortUrl", "clickCount", "createdAt" })
public class UrlShortenerModel {
	private Long id;
	private String originalUrl;
	private String shortUrl;
	private ZonedDateTime createdAt;
	private ZonedDateTime expiryAt;
	private Long clickCount;
}