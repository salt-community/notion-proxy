package com.saltpgp.notionproxy.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${API_KEY_HEADER}")
    private String API_KEY_HEADER;
    @Value("${CUSTOM_API_KEY}")
    private String CUSTOM_API_KEY;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        String requestApiKey = request.getHeader(API_KEY_HEADER);

        if (request.getRequestURI().contains("/swagger-ui") || request.getRequestURI().contains("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (CUSTOM_API_KEY.equals(requestApiKey)) {
            log.info("Successful API access: IP={}, Method={}, Path={}",
                    request.getRemoteAddr(), request.getMethod(), request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        log.warn("Unauthorized access attempt: IP={}, Method={}, Path={}, API Key Valid={}",
                request.getRemoteAddr(), request.getMethod(), request.getRequestURI(), false);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Unauthorized: Invalid API Key");

    }
}





