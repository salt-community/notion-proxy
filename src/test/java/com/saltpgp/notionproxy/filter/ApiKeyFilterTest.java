package com.saltpgp.notionproxy.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class ApiKeyFilterTest {

    private ApiKeyFilter apiKeyFilter;

    private final String API_KEY_HEADER = "header";

    private final String CUSTOM_API_KEY = "key";

    @BeforeEach
    void setUp() {
        apiKeyFilter = new ApiKeyFilter(API_KEY_HEADER,CUSTOM_API_KEY);
    }

    @Test
    void swaggerUiPathShouldPass() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/swagger-ui");

        FilterChain filterChain = mock(FilterChain.class);

        apiKeyFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void apiKeyValidShouldPass() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/some-protected-endpoint");
        request.addHeader(API_KEY_HEADER, CUSTOM_API_KEY);

        FilterChain filterChain = mock(FilterChain.class);

        apiKeyFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void apiKeyInvalidShouldFail() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/some-protected-endpoint");
        request.addHeader(API_KEY_HEADER, "invalid-api-key");

        FilterChain filterChain = mock(FilterChain.class);

        apiKeyFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(0)).doFilter(request, response);
        assert(response.getStatus() == HttpServletResponse.SC_UNAUTHORIZED);
    }
}
