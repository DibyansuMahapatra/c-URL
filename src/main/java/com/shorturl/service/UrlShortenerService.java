package com.shorturl.service;

import java.util.List;

import com.shorturl.dto.UrlShortenerDto;
import com.shorturl.model.GenericResponseModel;
import com.shorturl.model.UrlShortenerModel;

public interface UrlShortenerService {

    /**
     * Creates a short URL for a given original URL
     *
     * @param requestDto - Dto Object that brings the long/original URL
     * @return GenericResponseModel containing the saved UrlShortenerEntity
     */
    GenericResponseModel<UrlShortenerModel> createShortUrl(UrlShortenerDto requestDto);
    
    GenericResponseModel<List<UrlShortenerModel>> fetchAllShortUrls();
}