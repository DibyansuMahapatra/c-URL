package com.shorturl.model;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "statusCode", "status", "data", "errorMessage", "message" })
public class GenericResponseModel<T> {
	private int statusCode;
	private HttpStatus status;
	private T data;
	private String errorMessage;
	private String message;
}