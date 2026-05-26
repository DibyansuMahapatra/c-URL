package com.shorturl.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UrlShortenerDto {

	@NotBlank(message = "Original URL cannot be empty")
	private String originalUrl;

	// Optional custom alias
	@Size(max = 10, message = "Custom alias cannot exceed 10 characters")
	@Pattern(regexp = "^[a-zA-Z0-9_-]*$", message = "Custom alias can contain only letters, numbers, _ and -")
	private String customAlias;
}