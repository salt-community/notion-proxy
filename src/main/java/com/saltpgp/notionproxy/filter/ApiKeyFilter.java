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

    private final static String API_KEY_HEADER = "X-API-KEY";
    @Value("${CUSTOM_API_KEY}")
    private String CUSTOM_API_KEY;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        String requestApiKey = request.getHeader(API_KEY_HEADER);
        if (CUSTOM_API_KEY.equals(requestApiKey)) {
            log.info("Successful API access: IP={}, Method={}, Path={}",
                    request.getRemoteAddr(), request.getMethod(), request.getRequestURI());
            filterChain.doFilter(request, response);
        } else {
            log.warn("Unauthorized access attempt: IP={}, Method={}, Path={}, API Key Valid={}",
                    request.getRemoteAddr(), request.getMethod(), request.getRequestURI(), false);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid API Key");
        }
    }
}





