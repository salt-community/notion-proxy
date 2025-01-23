package com.saltpgp.notionproxy.bucket;


import org.springframework.stereotype.Component;

@Component
public class bucket {

    private final Storage storageClient;

    public bucket(
            storageClient = StorageOptions.newBuilder()
        .setHost(fakeGcsExternalUrl)
        .setProjectId("test-project")
        .setCredentials(NoCredentials.getInstance())
            .build()
        .getService();
    }

    public void stuff(){

    }
}
