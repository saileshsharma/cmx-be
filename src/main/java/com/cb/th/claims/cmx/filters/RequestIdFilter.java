package com.cb.th.claims.cmx.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import java.io.IOException;
import java.util.UUID;

public class RequestIdFilter implements Filter {
    @Override public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        String rid = UUID.randomUUID().toString();
        MDC.put("rid", rid);
        ((HttpServletResponse) res).setHeader("X-Request-Id", rid);
        try { chain.doFilter(req, res); } finally { MDC.remove("rid"); }
    }
}

