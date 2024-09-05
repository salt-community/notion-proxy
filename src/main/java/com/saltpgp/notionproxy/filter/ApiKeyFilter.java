package com.saltpgp.notionproxy.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {
    private static final String API_KEY_HEADER = "X-API-KEY";  // Header name where API key will be provided
    private static final String API_KEY = "your-large-secret-string-here"; // Your predefined secret key
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        String requestApiKey = request.getHeader(API_KEY_HEADER);
        String requestUri = request.getRequestURI();
        // Skip API key check for the login page
        if ("/login".equals(requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }
        if (API_KEY.equals(requestApiKey)) {
            System.out.println("API Key is valid. Proceeding...");
            // Continue with the filter chain if API key matches
            filterChain.doFilter(request, response);
        } else {
            System.out.println("Invalid API Key.");
            // Reject the request if the API key does not match
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid API Key");
        }
    }
}





