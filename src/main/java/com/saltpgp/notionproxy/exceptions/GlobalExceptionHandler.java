package com.saltpgp.notionproxy.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /***
     * Handle NotionNotFoundException - Returns 404 NOT FOUND
     */
    @ExceptionHandler({NotionNotFoundException.class})
    public ResponseEntity <Void> handleNotionNotFoundException(){return ResponseEntity.notFound().build();}


}
