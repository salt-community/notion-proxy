package com.saltpgp.notionproxy.bucket;


import com.google.cloud.NoCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Component;

@Component
public class bucket {

    private final Storage storageClient;

    private final String fakeGcsExternalUrl = "http://localhost:9000";

    public bucket() {
        storageClient = StorageOptions.newBuilder()
                .setHost(fakeGcsExternalUrl)
                .setProjectId("test-project")
                .setCredentials(NoCredentials.getInstance())
                .build()
                .getService();
    }

    public void stuff() {

    }
}
