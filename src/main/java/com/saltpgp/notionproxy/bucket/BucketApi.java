package com.saltpgp.notionproxy.bucket;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.NoCredentials;
import com.google.cloud.storage.*;
import org.apache.catalina.mapper.Mapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class BucketApi {

    private final Storage storageClient;

    private final String fakeGcsExternalUrl = "http://localhost:9000";

    String bucketName = "my-local-bucket";

    public BucketApi() {
        storageClient = StorageOptions.newBuilder()
                .setHost(fakeGcsExternalUrl)
                .setProjectId("test-project")
                .setCredentials(NoCredentials.getInstance())
                .build()
                .getService();
    }

    public void uploadFile(String cacheName, JsonNode jsonNode) {
        ObjectNode objectNode = (ObjectNode) jsonNode;
        objectNode.put("timestamp", System.currentTimeMillis());
        Bucket bucket = storageClient.get(bucketName);
        if (bucket == null) {
            bucket = storageClient.create(BucketInfo.of(bucketName));
            System.out.println("Bucket created: " + bucketName);
        }

        Blob blob = bucket.create(cacheName, objectNode.toString().getBytes(StandardCharsets.UTF_8));
        System.out.println("File uploaded to " + blob.getMediaLink());
    }

    public JsonNode getCache(String cacheName) {
        try {
            Bucket bucket = storageClient.get(bucketName);
            if (bucket == null) {
                System.out.println("Bucket not found: " + bucketName);
                return null;
            }

            Blob blob = bucket.get(cacheName);

            if (blob == null) {
                System.out.println("File not found: " + cacheName);
                return null;
            }
            byte[] content = blob.getContent();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(content);


            if (System.currentTimeMillis() - jsonNode.get("timestamp").asLong() > 60000) {
                return null;
            }
            return jsonNode;

        } catch (Exception e) {
            return null;
        }

    }
}
