package com.shorturl.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenericResponseModelList<T> extends GenericResponseModel<T> {
	private Long totalElements;
	private Long page;
	private Long size;
}