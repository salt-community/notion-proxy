package com.saltpgp.notionproxy.bucket;


import com.google.cloud.NoCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Component;

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

    public void uploadFile(String cacheName, Object cache) {
        Bucket bucket = storageClient.get(cacheName);
        if (bucket == null) {
            throw new RuntimeException("Bucket not found: " + cacheName);
        }

        Blob blob = bucket.create(cacheName, cache.toString().getBytes(StandardCharsets.UTF_8));
        System.out.println("File uploaded to " + blob.getMediaLink());
    }
}
