package com.saltpgp.notionproxy.staff;

import com.saltpgp.notionproxy.service.NotionApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StaffService {

    private NotionApiService notionApiService;
    private final String CORE_DATABASE_ID;

    public StaffService(NotionApiService notionApiService, @Value("${CORE_DATABASE_ID}")String coreDatabaseId) {
        this.notionApiService = notionApiService;
        CORE_DATABASE_ID = coreDatabaseId;
    }
}
