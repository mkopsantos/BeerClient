package com.mkop.beerclient.config;

import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.function.Function;

public class WebClientProperties {

    public static final String BASE_URL = "https://api.springframework.guru";
    public static final String LIST_BEER_PATH = "/api/v1/beer";
    public static final String BEER_BY_ID_PATH = "/api/v1/beer/{beerId}";
}
