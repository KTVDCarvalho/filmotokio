package filmotokio.security;

import filmotokio.service.UserService;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

// JWT request filter
// Processes JWT tokens in incoming requests
// Validates JWT tokens and sets up user authentication
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    // Service for loading user data
    @Autowired
    private UserService userService;

    // Utility for JWT token operations
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // JWT request filter
// Validates tokens on protected endpoints
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Get the Authorization header from the request
        final String requestTokenHeader = request.getHeader("Authorization");
        String requestURI = request.getRequestURI();
        
        // Skip JWT processing for login and register endpoints (they don't need authentication)
        if (requestURI.equals("/login") || requestURI.startsWith("/register")) {
            logger.debug("[JwtRequestFilter] - Skipping JWT processing for: {}", requestURI);
            chain.doFilter(request, response);
            return;
        }
        
        logger.debug("[JwtRequestFilter] - Processing request: {} {}", request.getMethod(), requestURI);
        logger.debug("[JwtRequestFilter] - Authorization header: {}", requestTokenHeader);

        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            // Handle duplicate "Bearer Bearer" issue
            String cleanToken = requestTokenHeader.replaceFirst("^Bearer\\s+", "");
            if (cleanToken.startsWith("Bearer ")) {
                jwtToken = cleanToken.substring(7); // Remove second "Bearer "
            } else {
                jwtToken = cleanToken;
            }
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                logger.debug("[JwtRequestFilter] - Extracted username from token: {}", username);
            } catch (Exception e) {
                logger.warn("[JwtRequestFilter] - Unable to get JWT Token: {}", e.getMessage());
            }
        } else {
            logger.debug("[JwtRequestFilter] - No Bearer token found in Authorization header");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                logger.debug("[JwtRequestFilter] - Authentication set for user: {}", username);
            } else {
                logger.warn("[JwtRequestFilter] - Token validation failed for user: {}", username);
            }
        }
        chain.doFilter(request, response);
    }
}
