package com.saltpgp.notionproxy.notionapi;

class NotionApiConstants {
    public static class UriNotionFormat{
        public static final String PAGES = "/pages/%s";
        public static final String DATABASES = "/databases/%s/query";
    }

    public static class HeaderValue{
        public static final String CONTENT_TYPE = "application/json";
        public static final String BEARER = "Bearer ";
    }

    public static class RequestHeader{
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String AUTHORIZATION = "Authorization";
        public static final String NOTION_VERSION = "Notion-Version";
    }

    public static class HttpClientCase {
        public static final String UNAUTHORIZED = "Unauthorized";
        public static final String BAD_REQUEST = "Bad Request";
        public static final String NOT_FOUND = "Not Found";
    }
    public static class ErrorNotionMessage{
        public static final String UNAUTHORIZED_TO_ACCESS = "Unauthorized to access Notion API. Check the API key.";
        public static final String BAD_REQUEST = "Bad request to the Notion API. Check the API request.";
        public static final String UNKNOWN_ERROR_WITH_HTTP_STATUS = "Unknown error occurred with HTTP status: ";
        public static final String CANT_ACCESS = "Can't access Notion API. Check if the Notion proxy can send requests.";
        public static final String UNKNOWN_ERROR_WHEN_SEND_REQUEST = "Unknown error occurred while trying to send request to Notion.";
        public static final String PAGE_ID_DIDNT_EXIST = "Page ID didn't exist in Notion: ";
        public static final String DATABASE_DIDNT_EXIST = "Database didn't exist in Notion";
    }
    public static class NotionType{
        public static final String PAGE = "page";
        public static final String DATABASE = "database";
    }
}
