package com.saltpgp.notionproxy.exceptions;

public class NotionException extends Exception {

    public NotionException() {
        super("An error occurred while interacting with the Notion API.");
    }

    public NotionException(String message) {
        super(message);
    }
}
