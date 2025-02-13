package com.saltpgp.notionproxy.exceptions;

public class NotionNotFoundException extends Exception{

    public NotionNotFoundException() {
        super("An error occurred while interacting with the Notion API.");
    }

    public NotionNotFoundException(String message) {
        super(message);
    }
}
