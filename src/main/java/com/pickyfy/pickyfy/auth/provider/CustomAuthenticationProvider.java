package com.pickyfy.pickyfy.auth.provider;

import com.pickyfy.pickyfy.auth.details.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private static final String INVALID_REQUEST = "잘못된 요청입니다.";
    private static final String USER_NOT_FOUND = "존재하지 않는 유저입니다.";
    private static final String PASSWORD_WRONG = "잘못된 비밀번호입니다.";

    private final CustomUserDetailsService customUserDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if(authentication.getPrincipal() == null || authentication.getCredentials() == null){
            throw new AuthenticationServiceException(INVALID_REQUEST);
        }
            String principal = authentication.getPrincipal().toString();
            String password = authentication.getCredentials().toString();

            UserDetails userDetails = loadUserByDetails(principal);
            validatePassword(password, userDetails.getPassword());

            return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private UserDetails loadUserByDetails(String principal) {
        try{
            if(isEmail(principal)){
                return customUserDetailsService.loadUserByEmail(principal);
            }
            return customUserDetailsService.loadUserByUsername(principal);
        }catch (Exception e){
            throw new UsernameNotFoundException(USER_NOT_FOUND);
        }
    }

    private boolean isEmail(String principal){
        return principal.contains("@");
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
             throw new BadCredentialsException(PASSWORD_WRONG);
        }
    }
}
