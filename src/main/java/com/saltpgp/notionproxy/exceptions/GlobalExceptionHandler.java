package com.saltpgp.notionproxy.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /***
     * Handle NotionNotFoundException - Returns 404 NOT FOUND
     */
    @ExceptionHandler({NotionNotFoundException.class})
    public ResponseEntity<Void> handleNotionNotFoundException() {
        log.debug("NotionNotFoundException was thrown: Resource not found");
        return ResponseEntity.notFound().build();
    }


    /**
     * Handle NotionException - Returns 500 INTERNAL SERVER ERROR
     */
    @ExceptionHandler({NotionException.class})
    public ResponseEntity<String> handleNotionException(NotionException e) {
        log.error("NotionException encountered: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An internal error occurred: " + e.getMessage());
    }

    /**
     * Handle Generic Exception - Returns 500 INTERNAL SERVER ERROR
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<String> handleGenericException(Exception e) {
        log.error("Generic exception encountered: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + e.getMessage());
    }
}
