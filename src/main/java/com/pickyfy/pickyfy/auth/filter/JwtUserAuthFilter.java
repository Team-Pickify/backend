package com.pickyfy.pickyfy.auth.filter;

import com.pickyfy.pickyfy.auth.custom.CustomUserDetails;
import com.pickyfy.pickyfy.auth.custom.CustomUserDetailsService;
import com.pickyfy.pickyfy.auth.util.AuthorityConstant;
import com.pickyfy.pickyfy.auth.util.JwtUserUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtUserAuthFilter extends OncePerRequestFilter implements JwtAuthFilter {

    private static final String REQUIRED_ROLE = "ROLE_USER";
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUserUtil jwtUserUtil;

    @Override
    public void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);
        if (token == null || !jwtUserUtil.validateToken(token)){
            filterChain.doFilter(request, response);
            return;
        }

        String email = jwtUserUtil.getUserEmail(token);
        CustomUserDetails userDetails = loadUserDetails(email);

        if (userDetails == null){
            filterChain.doFilter(request, response);
            return;
        }

        if (!hasRequiredRole(userDetails)){
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "거부된 사용자 권한입니다.");
            return;
        }

        setAuthentication(userDetails);
        filterChain.doFilter(request, response);
    }

    @Override
    public String resolveToken(HttpServletRequest request){
        String authorizationHeader = request.getHeader(AuthorityConstant.ACCESS_TOKEN_HEADER);
        if (authorizationHeader == null || !authorizationHeader.startsWith(AuthorityConstant.BEARER_PREFIX)){
            return null;
        }
        return authorizationHeader.substring(AuthorityConstant.BEARER_LENGTH); // bearer 이후 부분 차출
    }

    private CustomUserDetails loadUserDetails(String email){
        try{
            return (CustomUserDetails) customUserDetailsService.loadUserByEmail(email);
        } catch (UsernameNotFoundException e){
            return null;
        }
    }

    private boolean hasRequiredRole(CustomUserDetails userDetails){
        return userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(REQUIRED_ROLE));
    }

    private void setAuthentication(CustomUserDetails userDetails){
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }
}
