package com.pickyfy.pickyfy.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickyfy.pickyfy.auth.filter.CustomLoginFilter;
import com.pickyfy.pickyfy.auth.resolver.CustomAuthorizationRequestResolver;
import com.pickyfy.pickyfy.auth.handler.OAuth2FailureHandler;
import com.pickyfy.pickyfy.auth.handler.OAuth2SuccessHandler;
import com.pickyfy.pickyfy.auth.filter.JwtAuthFilter;
import com.pickyfy.pickyfy.common.util.JwtUtil;
import com.pickyfy.pickyfy.auth.custom.CustomUserDetailsService;
import com.pickyfy.pickyfy.common.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthorizationRequestResolver customAuthorizationRequestResolver;
    private final DefaultOAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final ObjectMapper objectMapper;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        CustomLoginFilter customLoginFilter
                = new CustomLoginFilter(authenticationManager(authenticationConfiguration), objectMapper, jwtUtil, redisUtil);
        customLoginFilter.setFilterProcessesUrl("/auth/login");

        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/users/signup", "/auth/login","/admin", "/email-auth/**").permitAll()
                        .requestMatchers("/auth/oauth2/**", "/oauth2/callback/**").permitAll()
                        .requestMatchers( "/","/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated());

        http
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestResolver(customAuthorizationRequestResolver))
                        .redirectionEndpoint(url -> url.baseUri("/oauth2/callback"))
                        .userInfoEndpoint(endpoint -> endpoint.userService(oAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler));

        http
                .addFilterAt(customLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthFilter(customUserDetailsService, jwtUtil), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}