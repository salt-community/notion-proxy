package com.saltpgp.notionproxy.notionapi;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestClientInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        System.out.println("RestClientInterceptor.intercept");
        System.out.println("request.getURI() = " + request.getURI());
        System.out.println("request.getMethod() = " + request.getMethod());
        String strBody = new String(body);
        System.out.println("strBody = " + strBody);
        return execution.execute(request, body);
    }
}
