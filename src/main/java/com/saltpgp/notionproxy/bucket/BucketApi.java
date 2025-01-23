package com.saltpgp.notionproxy.bucket;


import com.google.cloud.NoCredentials;
import com.google.cloud.storage.*;
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
        Bucket bucket = storageClient.get(bucketName);
        if (bucket == null) {
            bucket = storageClient.create(BucketInfo.of(bucketName));
            System.out.println("Bucket created: " + bucketName);
        }

        Blob blob = bucket.create(cacheName, cache.toString().getBytes(StandardCharsets.UTF_8));
        System.out.println("File uploaded to " + blob.getMediaLink());
    }
}
