package com.saltpgp.notionproxy.developer.service;

import com.saltpgp.notionproxy.service.NotionApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DeveloperService {

    private final NotionApiService notionApiService;
    private final String DATABASE_ID;
    private final String SCORE_DATABASE_ID;
    private final String CORE_DATABASE_ID;

    public DeveloperService(NotionApiService notionApiService,
                            @Value("${DATABASE_ID}") String DATABASE_ID,
                            @Value("${SCORE_DATABASE_ID}") String SCORE_DATABASE_ID,
                            @Value("${CORE_DATABASE_ID}") String CORE_DATABASE_ID) {
        this.notionApiService = notionApiService;
        this.DATABASE_ID = DATABASE_ID;
        this.SCORE_DATABASE_ID = SCORE_DATABASE_ID;
        this.CORE_DATABASE_ID = CORE_DATABASE_ID;

    }
}
