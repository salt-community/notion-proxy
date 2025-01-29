package com.saltpgp.notionproxy.api.bucket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import org.springframework.test.web.client.MockRestServiceServer;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(BucketApiService.class)
class BucketApiServiceTest {

    @Autowired
    MockRestServiceServer server;

    @Autowired
    private BucketApiService bucketApiService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${CACHE_EXPIRE_IN_MILLISECONDS}")
    private long CACHE_EXPIRE_IN_MILLISECONDS;

    @Value("${SUPABASE_ANON_KEY}")
    private String SUPABASE_ANON_KEY;

    @Value("${SUPABASE_URL}")
    private String SUPABASE_URL;



    @Test
    void saveCache() throws Exception {
        String cacheName = "testCache";
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("key", "value");

        server.expect(requestTo(SUPABASE_URL + "/storage/v1/object/notion-proxy-cache/" + cacheName))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(header("Authorization", "Bearer " + SUPABASE_ANON_KEY))
                .andExpect(header("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withNoContent());

        bucketApiService.saveCache(cacheName, jsonNode);

        server.verify();
    }

    @Test
    void getCache_NotExpired() throws Exception {
        String cacheName = "testCache";
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("key", "value");
        jsonNode.put("timestamp", System.currentTimeMillis());

        byte[] responseBody = objectMapper.writeValueAsBytes(jsonNode);

        server.expect(requestTo(SUPABASE_URL + "/storage/v1/object/notion-proxy-cache/" + cacheName))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + SUPABASE_ANON_KEY))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        JsonNode result = bucketApiService.getCache(cacheName);

        assertNotNull(result);
        assertEquals("value", result.get("key").asText());

        server.verify();
    }

    @Test
    void getCache_Expired() throws Exception {
        String cacheName = "testCache";
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("key", "value");
        jsonNode.put("timestamp", System.currentTimeMillis() - (CACHE_EXPIRE_IN_MILLISECONDS + 1000));

        byte[] responseBody = objectMapper.writeValueAsBytes(jsonNode);

        server.expect(requestTo(SUPABASE_URL + "/storage/v1/object/notion-proxy-cache/" + cacheName))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + SUPABASE_ANON_KEY))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        JsonNode result = bucketApiService.getCache(cacheName);

        assertNull(result);
        server.verify();
    }

    @Test
    void getCache_Failure() {
        String cacheName = "testCache";

        server.expect(requestTo(SUPABASE_URL + "/storage/v1/object/notion-proxy-cache/" + cacheName))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        JsonNode result = bucketApiService.getCache(cacheName);

        assertNull(result);
        server.verify();
    }
}
