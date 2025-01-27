package com.pickyfy.pickyfy.auth.filter;

import com.pickyfy.pickyfy.common.util.JwtUtil;
import com.pickyfy.pickyfy.auth.custom.CustomUserDetails;
import com.pickyfy.pickyfy.auth.custom.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String ACCESS_TOKEN_HEADER = "Authorization";
    private static final String BEARER = "Bearer ";

    private final CustomUserDetailsService customUserDetailsService;
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
            // 여기에 리프레시토큰 확인하고 있으면 재발급하는 코드 들어가야하나
            filterChain.doFilter(request, response);
            return;
        }
        String email = jwtUtil.getUserEmail(token);
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByEmail(email);

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