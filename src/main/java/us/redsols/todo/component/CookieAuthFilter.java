package us.redsols.todo.component;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import us.redsols.todo.config.JwtTokenProvider;

// @Component
public class CookieAuthFilter implements Filter {

    private final JwtTokenProvider jwtTokenProvider;

    public CookieAuthFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Let preflight (OPTIONS) requests pass through
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String requestPath = req.getRequestURI();

        if (requiresAuth(requestPath)) {
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("access_token".equals(cookie.getName())) {
                        String token = cookie.getValue();

                        // Validate token
                        if (jwtTokenProvider.validateToken(token)) {
                            String uid = jwtTokenProvider.extractUid(token);
                            req.setAttribute("uid", uid); // Set user ID for downstream use
                            chain.doFilter(req, res); // Continue request processing
                            return;
                        }
                    }
                }
            }

            // If no valid cookie is found, respond with 401
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("Authentication required");
            return;
        }

        // If the request does not require authentication, continue processing
        chain.doFilter(request, response);
    }

    private boolean requiresAuth(String path) {
        // Add routes that require authentication
        return path.startsWith("/todos") || path.startsWith("/user");
    }

}
