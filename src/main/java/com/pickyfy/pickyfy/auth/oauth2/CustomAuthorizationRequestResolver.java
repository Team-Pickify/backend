package com.pickyfy.pickyfy.auth.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private static final String AUTHORIZATION_REQUEST_BASE_URL = "/auth/oauth2";
    private static final String QUERY_PARAM = "redirect";

    private final OAuth2AuthorizationRequestResolver defaultOAuth2AuthorizationRequestResolver;

    public CustomAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository){
        this.defaultOAuth2AuthorizationRequestResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, AUTHORIZATION_REQUEST_BASE_URL);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest
                = defaultOAuth2AuthorizationRequestResolver.resolve(request);
        return setRedirect(request, authorizationRequest);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest
                = defaultOAuth2AuthorizationRequestResolver.resolve(request, clientRegistrationId);
        return setRedirect(request, authorizationRequest);
    }

    private OAuth2AuthorizationRequest setRedirect(HttpServletRequest request, OAuth2AuthorizationRequest authorizationRequest){
        if (authorizationRequest == null) {
            return null;
        }
        String redirect = request.getParameter(QUERY_PARAM);
        if (redirect != null) {
            request.getSession().setAttribute(QUERY_PARAM, redirect);
        }
        return authorizationRequest;
    }
}
