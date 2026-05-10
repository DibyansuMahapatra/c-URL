package com.shorturl.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UrlShortenerDto {
	
	private String originalUrl;
	private String shortUrl;
	private Integer page;
	private Integer size;
}