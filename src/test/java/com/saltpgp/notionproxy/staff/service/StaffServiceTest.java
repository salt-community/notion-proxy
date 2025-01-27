package com.saltpgp.notionproxy.staff.service;

import com.saltpgp.notionproxy.notionapi.NotionApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

public class StaffServiceTest {

    private NotionApiService mockApiService;

    @Value("${CORE_DATABASE_ID}")
    private String mockCoreDatabaseId;
    @Value("${DATABASE_ID}")
    private String mockDeveloperDatabaseId;

    @Test
    void getAllCoreShouldReturnAllCore() {

    }
}
