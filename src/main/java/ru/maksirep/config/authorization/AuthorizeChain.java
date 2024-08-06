package ru.maksirep.config.authorization;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.maksirep.core.service.AuthorizeService;

import java.io.IOException;

@Component
public class AuthorizeChain extends OncePerRequestFilter {

    private final AuthorizeService authorizeService;

    public AuthorizeChain(AuthorizeService authorizeService) {
        this.authorizeService = authorizeService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token == null) {
            chain.doFilter(request, response);
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(token));
        chain.doFilter(request, response);
    }

    private Authentication getAuthentication(String token) {
        return new JwtAuthentication(token, authorizeService.validateTokenAndGetUserId(token));
    }
}
