package com.pickyfy.pickyfy.config;

import com.pickyfy.pickyfy.auth.filter.JwtAdminAuthFilter;
import com.pickyfy.pickyfy.auth.filter.JwtUserAuthFilter;
import com.pickyfy.pickyfy.auth.util.JwtAdminUtil;
import com.pickyfy.pickyfy.auth.util.JwtUserUtil;
import com.pickyfy.pickyfy.auth.custom.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAdminUtil jwtAdminUtil;
    private final JwtUserUtil jwtUserUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/users/signup", "/auth/login/**", "/admin").permitAll()
                        .requestMatchers( "/","/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated());

        http
                .addFilterBefore(new JwtUserAuthFilter(customUserDetailsService, jwtUserUtil), JwtUserAuthFilter.class)
                .addFilterBefore(new JwtAdminAuthFilter(customUserDetailsService, jwtAdminUtil), JwtAdminAuthFilter.class);
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}