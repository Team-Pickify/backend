package com.pickyfy.pickyfy.auth.custom;

import com.pickyfy.pickyfy.domain.Provider;
import com.pickyfy.pickyfy.domain.User;
import com.pickyfy.pickyfy.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest); // 엑세스토큰은 발급된 상태 카카오 인증서버에서 값 가져와야함

        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");

        Long id = (Long) attributes.get("id");
        String email = account.get("email").toString();
        String nickname = properties.get("nickname").toString();
        String profileImage = properties.get("thumbnail_image").toString();

        saveOrUpdateUser(id, email, profileImage, nickname);

        Map<String, Object> modifiedAttributes = new HashMap<>(attributes);
        modifiedAttributes.put("email", email);

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                modifiedAttributes,
                "email"
        );
    }

    private void saveOrUpdateUser(Long id, String email, String image, String nickname) {
        User user = userRepository.findByEmail(email).orElseGet(() ->
                User.builder()
                        .email(email) // 새로운 이메일
                        .nickname(nickname) // 새로운 닉네임
                        .profileImage(image) // 새로운 프로필 이미지
                        .provider(Provider.KAKAO) // 새로운 provider
                        .providerId(id) // 새로운 providerId
                        .build()
        );
        userRepository.save(user); // 변경된 사항 저장
    }
}