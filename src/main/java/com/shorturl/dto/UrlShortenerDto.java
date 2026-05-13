package com.shorturl.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UrlShortenerDto {

	@NotBlank(message = "Original URL cannot be empty")
	private String originalUrl;
}