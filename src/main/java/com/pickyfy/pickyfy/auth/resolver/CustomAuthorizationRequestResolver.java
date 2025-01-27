package com.pickyfy.pickyfy.auth.resolver;

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

    /*
    OAuth2AuthorizationRequestResolver는 인터페이스
    DefaultOAuth2AuthorizationRequestResolver는 구현체 Default~~ 사용하기 위해
    ClientRegistrationRepository는 추출한 registrationId 값으로 ClientRegistrationRepository.findByRegistrationId("kakao") 호출. ClientRegistration 반환:
    ClientRegistrationRepository이거 주입을 어디서 해야하는거 아닌가 @Bean마냥?
     */
    public CustomAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository){
        this.defaultOAuth2AuthorizationRequestResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, AUTHORIZATION_REQUEST_BASE_URL);
    }

    /*
    자동 추출 메서드
    // 여기서 kakao 추출하고 그걸 기반으로 OAuth 2.0 인증 요청 객체 생성
     */
    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) { // default에는 이미 yml값들 들어가있음
        OAuth2AuthorizationRequest authorizationRequest
                = defaultOAuth2AuthorizationRequestResolver.resolve(request);
        return setRedirect(request, authorizationRequest);
    }

    /*
    명시적 지정 메서드
    명시적으로 지정한걸로 OAuth 2.0 인증 요청 객체 생성
     */
    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest
                = defaultOAuth2AuthorizationRequestResolver.resolve(request, clientRegistrationId);
        return setRedirect(request, authorizationRequest);
    }

    /*
    소셜 로그인 최종 완료 후 갈 redirect 주소 홈화면이면 home 일수도있고
     */
    private OAuth2AuthorizationRequest setRedirect(HttpServletRequest request, OAuth2AuthorizationRequest authorizationRequest){

        if (authorizationRequest == null) {
            return null;
        }

        String redirect = request.getParameter(QUERY_PARAM);
        if (redirect != null) {
            request.getSession().setAttribute(QUERY_PARAM, redirect); //getSession 왜있지
        }

        return authorizationRequest;

    }
}
