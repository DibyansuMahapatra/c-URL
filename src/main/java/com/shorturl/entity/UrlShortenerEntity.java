package com.shorturl.entity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "url_shortener")

@Getter
@Setter
@NoArgsConstructor
public class UrlShortenerEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "original_url", nullable = false, unique = true, length = 2048)
	private String originalUrl;

	@Column(name = "short_code", nullable = false, unique = true, length = 10)
	private String shortCode;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "expires_at", nullable = false, updatable = false)
	private Instant expiresAt;

	@Column(name = "click_count", nullable = false)
	private Long clickCount = 0L;

	// Automatically set expiresAt to 2 hours after creation
	@PrePersist
	public void prePersist() {
		if (expiresAt == null) {
			expiresAt = Instant.now().plus(2, ChronoUnit.HOURS);
		}
	}
}