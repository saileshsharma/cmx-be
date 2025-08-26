package com.cb.th.claims.cmx.filters;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.cb.th.claims.cmx.util.LogManager.edgeLog;


@Component
@WebFilter(urlPatterns = "/graphql")
public class GraphQLLoggingFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().contains("/graphql");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(requestWrapper, responseWrapper);

        String requestBody = new String(requestWrapper.getContentAsByteArray(), request.getCharacterEncoding());
        String responseBody = new String(responseWrapper.getContentAsByteArray(), response.getCharacterEncoding());

        String requestHeaders = Collections.list(request.getHeaderNames()).stream().map(header -> header + "=" + request.getHeader(header)).collect(Collectors.joining(", "));

        String responseHeaders = response.getHeaderNames().stream().map(header -> header + "=" + response.getHeader(header)).collect(Collectors.joining(", "));

        edgeLog.info("GRAPHQL REQUEST HEADERS: {}", requestHeaders);
        edgeLog.info("GRAPHQL REQUEST BODY: {}", requestBody);
        edgeLog.info("GRAPHQL RESPONSE HEADERS: {}", responseHeaders);
        edgeLog.info("GRAPHQL RESPONSE BODY: {}", responseBody);

        responseWrapper.copyBodyToResponse(); // ✅ Needed to prevent blank response
    }
}
