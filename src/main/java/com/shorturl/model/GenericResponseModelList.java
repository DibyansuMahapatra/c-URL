package com.shorturl.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({ "statusCode", "status", "data", "errorMessage", "message", "totalElements", "page", "size" })
public class GenericResponseModelList<T> extends GenericResponseModel<T> {

	private Long totalElements;
	private Long page;
	private Long size;
}