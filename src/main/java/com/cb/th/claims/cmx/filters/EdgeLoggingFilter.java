package com.cb.th.claims.cmx.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.cb.th.claims.cmx.util.LogManager.edgeLog;


@Component
public class EdgeLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        String ip = req.getRemoteAddr();
        String method = req.getMethod();
        String uri = req.getRequestURI();
        String agent = req.getHeader("User-Agent");

        edgeLog.info("EDGE REQUEST → [{}] {} from IP={} User-Agent={}", method, uri, ip, agent);

        chain.doFilter(request, response);
    }
}
