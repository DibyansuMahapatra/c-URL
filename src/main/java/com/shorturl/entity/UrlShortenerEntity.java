package com.shorturl.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "url_shortener")
//@JsonPropertyOrder({ "id", "originalUrl", "shortUrl", "clickCount", "createdAt" })

@Getter
@Setter
@NoArgsConstructor
public class UrlShortenerEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "original_url", nullable = false)
	private String originalUrl;

	@Column(name = "short_url", nullable = false, unique = true)
	private String shortUrl;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "click_count", nullable = false)
	private Long clickCount = 0L;

	// Business logic method
	public void incrementClickCount() {
		this.clickCount++;
	}
}