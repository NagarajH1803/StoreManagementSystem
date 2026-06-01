package com.shopmanagement.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.UUID;

/**
 * CSRF protection filter. Generates a token on session creation
 * and validates it on every POST request.
 */
@WebFilter(urlPatterns = "/*")
public class CsrfFilter implements Filter {

    private static final String CSRF_TOKEN_ATTR = "csrfToken";
    private static final String CSRF_PARAM = "_csrf";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // Skip static resources and API endpoints
        String path = request.getServletPath();
        if (path.startsWith("/assets/") || path.startsWith("/api/")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(true);

        // Generate CSRF token if not present
        if (session.getAttribute(CSRF_TOKEN_ATTR) == null) {
            session.setAttribute(CSRF_TOKEN_ATTR, UUID.randomUUID().toString());
        }

        // Validate CSRF token on POST requests
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String sessionToken = (String) session.getAttribute(CSRF_TOKEN_ATTR);
            String requestToken = request.getParameter(CSRF_PARAM);

            if (sessionToken == null || !sessionToken.equals(requestToken)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
