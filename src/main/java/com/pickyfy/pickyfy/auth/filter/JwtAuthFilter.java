package com.pickyfy.pickyfy.auth.filter;

import com.pickyfy.pickyfy.common.util.JwtUtil;
import com.pickyfy.pickyfy.auth.details.CustomUserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String ACCESS_TOKEN_HEADER = "Authorization";
    private static final String BEARER = "Bearer ";

    private final CustomUserDetailsServiceImpl customUserDetailsServiceImpl;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(ACCESS_TOKEN_HEADER);

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authorizationHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }
        String principal = jwtUtil.getPrincipal(token);
        String role = jwtUtil.getRole(token);
        UserDetails userDetails;

        if(role.equals("ADMIN")){
             userDetails = customUserDetailsServiceImpl.loadUserByUsername(principal);
        }else {
            userDetails = customUserDetailsServiceImpl.loadUserByEmail(principal);
        }

        if (userDetails == null) {
            filterChain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        filterChain.doFilter(request, response);
    }
}