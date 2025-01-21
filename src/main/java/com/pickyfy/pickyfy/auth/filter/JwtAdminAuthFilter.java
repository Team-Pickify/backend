package com.pickyfy.pickyfy.auth.filter;

import com.pickyfy.pickyfy.auth.custom.AdminDetails;
import com.pickyfy.pickyfy.auth.custom.CustomUserDetailsService;
import com.pickyfy.pickyfy.auth.util.AuthorityConstant;
import com.pickyfy.pickyfy.auth.util.JwtAdminUtil;
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
public class JwtAdminAuthFilter extends OncePerRequestFilter implements JwtAuthFilter {

    private static final String REQUIRED_ROLE = "ROLE_ADMIN";
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAdminUtil jwtAdminUtil;

    @Override
    public void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);
        // 토큰 인증이 안 된 경우에는 다음 필터 체인으로 넘어가도록
        if (token == null || !jwtAdminUtil.validateToken(token)){
            filterChain.doFilter(request, response);
            return;
        }

        String adminName = jwtAdminUtil.getAdminName(token);
        AdminDetails adminDetails = loadAdminDetails(adminName);

        // 권한 부족인 경우에는 그냥 체인 중단시켜버림
        if (adminDetails == null || !hasRequiredRole(adminDetails)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "거부된 관리자 권한입니다.");
            return;
        }

        setAuthentication(adminDetails);
        filterChain.doFilter(request, response);
    }

    @Override
    public String resolveToken(HttpServletRequest request){
        String authorizationHeader = request.getHeader(AuthorityConstant.ACCESS_TOKEN_HEADER);
        if (authorizationHeader != null && authorizationHeader.startsWith(AuthorityConstant.BEARER_PREFIX)){
            return authorizationHeader.substring(AuthorityConstant.BEARER_LENGTH);
        }
        return null;
    }

    private AdminDetails loadAdminDetails(String adminName){
        try{
            return (AdminDetails) customUserDetailsService.loadUserByUsername(adminName);
        } catch (UsernameNotFoundException e){
            return null;
        }
    }

    private boolean hasRequiredRole(AdminDetails adminDetails){
        return adminDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(REQUIRED_ROLE));
    }

    private void setAuthentication(AdminDetails adminDetails){
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(adminDetails, null, adminDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }
}
