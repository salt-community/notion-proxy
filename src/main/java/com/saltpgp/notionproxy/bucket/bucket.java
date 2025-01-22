package com.saltpgp.notionproxy.bucket;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class bucket {
    private final RestClient restClient;
    private final String ENDPOINT;
    private final String ACCESS_KEY;
    private final String SECRET_KEY;
    private final String BUCKET_NAME;

    public bucket(RestClient.Builder builder,
                  @Value("${BUCKET_ENDPOINT}") String ENDPOINT,
                  @Value("${BUCKET_ACCESS_KEY}") String ACCESS_KEY,
                  @Value("${BUCKET_SECRET_KEY}") String SECRET_KEY,
                  @Value("${BUCKET_BUCKET_NAME}") String BUCKET_NAME) {
        this.restClient = builder.baseUrl(ENDPOINT).build();
        this.ENDPOINT = ENDPOINT;
        this.ACCESS_KEY = ACCESS_KEY;
        this.SECRET_KEY = SECRET_KEY;
        this.BUCKET_NAME = BUCKET_NAME;
    }

    public void stuff(){

    }
}
