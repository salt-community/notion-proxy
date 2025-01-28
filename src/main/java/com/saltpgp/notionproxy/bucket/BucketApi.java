package com.saltpgp.notionproxy.bucket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class BucketApi {

    private final String SUPABASE_ANON_KEY;
    private final long CACHE_EXPIRE_IN_MILLISECONDS;

    private final String BUCKET_NAME = "notion-proxy-cache";

    private final RestClient restClient;

    public BucketApi(@Value("${CACHE_EXPIRE_IN_MILLISECONDS}") long cacheExpireInMilliseconds,
                     @Value("${SUPABASE_URL}") String supabaseUrl,
                     @Value("${SUPABASE_ANON_KEY}") String supabaseAnonKey,
                     RestClient.Builder builder) {
        CACHE_EXPIRE_IN_MILLISECONDS = cacheExpireInMilliseconds;
        SUPABASE_ANON_KEY = supabaseAnonKey;
        this.restClient = builder.baseUrl(supabaseUrl).build();
    }

    public void saveCache(String cacheName, JsonNode jsonNode) {
        try {
            ObjectNode objectNode = (ObjectNode) jsonNode;
            objectNode.put("timestamp", System.currentTimeMillis());

            restClient
                    .put()
                    .uri( "/storage/v1/object/" + BUCKET_NAME+"/"+ cacheName)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                    .body(objectNode.toString().getBytes(StandardCharsets.UTF_8))
                    .retrieve()
                    .body(Void.class);
            log.debug("File uploaded to Supabase storage");
        } catch (Exception e) {
            log.warn("Failed to upload file to Supabase storage: {}", e.getMessage());
        }
    }

    public JsonNode getCache(String cacheName) {
        try {
            var result =  restClient
                    .get()
                    .uri("/storage/v1/object/" + BUCKET_NAME+"/"+ cacheName)
                    .header("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                    .retrieve()
                    .body(byte[].class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(result);
            if (System.currentTimeMillis() - jsonNode.get("timestamp").asLong() > CACHE_EXPIRE_IN_MILLISECONDS) {
                log.debug("Cache expired");
                return null;
            }
            return jsonNode;
        } catch (Exception e) {
            log.warn("Failed to retrieve cache: {}", e.getMessage());
        }
        return null;
    }
}
