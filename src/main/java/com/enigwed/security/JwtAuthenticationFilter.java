package com.enigwed.security;

import com.enigwed.constant.SErrorMessage;
import com.enigwed.dto.JwtClaim;
import com.enigwed.exception.JwtAuthenticationException;
import com.enigwed.service.UserCredentialService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserCredentialService userCredentialService;
    private final JwtUtil jwtUtil;

    private static final List<String> EXEMPT_PATHS = Arrays.asList(
            "/api/auth/**",
            "/api/public/orders/**",
            "/api/public/**",
            "/static/**",
            "/images/**",
            "/banks/**",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    );

    // Path matcher to handle wildcard matching like "/api/public/**"
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // Check if the request URL matches any exempt paths
            if (isExemptPath(request.getRequestURI())) {
                filterChain.doFilter(request, response);  // Skip authentication for exempt paths
                return;
            }

            // Process JWT token for non-exempt paths
            String token = parseJwt(request);
            if (token != null && jwtUtil.verifyJwtToken(token)) {
                JwtClaim userInfo = jwtUtil.getUserInfoByToken(token);
                UserDetails user = userCredentialService.loadUserByUsername(userInfo.getEmail());
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );

                authenticationToken.setDetails(new WebAuthenticationDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                throw new JwtAuthenticationException(SErrorMessage.JWT_INVALID);
            }
        } catch (Exception e) {
            log.error("Error in JWT authentication: {}", e.getMessage());
            throw new JwtAuthenticationException(SErrorMessage.JWT_AUTHENTICATION_FAILED);
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }

    // Check if the request URI matches any exempt paths
    private boolean isExemptPath(String requestUri) {
        // Iterate through the exempt paths and check for matches
        for (String exemptPath : EXEMPT_PATHS) {
            if (pathMatcher.match(exemptPath, requestUri)) {
                return true;
            }
        }
        return false;
    }

    private String parseJwt(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }
}



