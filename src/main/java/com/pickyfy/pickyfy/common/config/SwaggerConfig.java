package com.pickyfy.pickyfy.common.config;

import com.pickyfy.pickyfy.auth.filter.CustomLoginFilter;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import java.util.Optional;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI pickifyAPI() {

        Info info = new Info()
                .title("Pickify Server API")
                .description("Pickify Server API 명세서")
                .version("1.0.0");

        String jwtSchemeName = "JWT TOKEN";

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        Server httpsServer = new Server();
        httpsServer.setDescription("pickyfy 서버입니다.");

        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(components);
    }

    @Bean
    OpenApiCustomizer springSecurityLoginEndpointCustomizer(ApplicationContext applicationContext) {
        FilterChainProxy filterChainProxy = applicationContext.getBean(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME, FilterChainProxy.class);
        return openApi -> {
            for (SecurityFilterChain filterChain : filterChainProxy.getFilterChains()) {
                Optional<CustomLoginFilter> optionalFilter =
                        filterChain.getFilters().stream()
                                .filter(CustomLoginFilter.class::isInstance)
                                .map(CustomLoginFilter.class::cast)
                                .findAny();

                if (optionalFilter.isPresent()) {
                    CustomLoginFilter customLoginFilter = optionalFilter.get();

                    Operation operation = new Operation()
                            .summary("로그인 API")
                            .description("""
                                    로그인 API입니다.
                                    - 사용자는 `email`을 입력하세요.
                                    - 관리자는 `name`을 입력하세요.
                                    (JSON key는 항상 `principal`로 고정)

                                    Cookie 값을 확인하려면:
                                    1. F12를 눌러 개발자 도구를 엽니다.
                                    2. Application 탭으로 이동합니다.
                                    3. Cookies 섹션에서 값을 확인하세요.
                                    """);

                    Schema<?> schema = new ObjectSchema()
                            .addProperty("principal", new StringSchema()._default("pickyfy@gmail.com"))
                            .addProperty(customLoginFilter.getPasswordParameter(), new StringSchema()._default("1aA!1aA!"));

                    RequestBody requestBody = new RequestBody().content(new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, new MediaType().schema(schema)));
                    operation.requestBody(requestBody);

                    ApiResponses apiResponses = new ApiResponses();
                    apiResponses.addApiResponse(String.valueOf(HttpStatus.OK.value()),
                            new ApiResponse()
                                    .description(HttpStatus.OK.getReasonPhrase())
                                    .addHeaderObject("Authorization", new Header()
                                            .description("Access token")
                                            .schema(new StringSchema()
                                                    ._default("Bearer jwt-access-token")))
                                    .addHeaderObject("Set-Cookie", new Header()
                                            .description("Refresh token")
                                            .schema(new StringSchema()
                                                    ._default("refreshToken=abcd1234; Path=/; HttpOnly; Secure; SameSite=Strict")))
                    );

                    apiResponses.addApiResponse(String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                            new ApiResponse().description(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                                    .content(new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                            new MediaType().example("{"
                                                    + "\"error\": \"존재하지 않는 유저입니다.\","
                                                    + "\"status\": 401,"
                                                    + "\"errorCode\": 4001"
                                                    + "}"
                                            ))));

                    operation.responses(apiResponses);
                    operation.addTagsItem("일반 로그인");
                    operation.summary("로그인");

                    PathItem pathItem = new PathItem().post(operation);
                    openApi.getPaths().addPathItem("/auth/login", pathItem);
                }
            }
        };
    }

    @Bean
    public OpenApiCustomizer oauth2EndpointCustomizer() {
        return openApi -> {
            Operation authOperation = new Operation()
                    .summary("카카오 OAuth2 인증 요청")
                    .description("""
                    카카오 인증을 요청합니다.
                    - Swagger는 AJAX 요청을 사용하므로, 브라우저가 302 Redirect를
                      기본 페이지 이동으로 처리하지 않습니다.
                    - 테스트를 위해 브라우저에서 엔드포인트 URL을 직접 입력하여
                      카카오 로그인 화면으로 이동해야 합니다.
                    - 로그인 성공 시 서버는 엑세스토큰을 Authorization 헤더에 리프레시토큰을 httpOnly 쿠키로 반환합니다.
                    """);

            PathItem authPathItem = new PathItem().get(authOperation);
            openApi.getPaths().addPathItem("/auth/oauth2/kakao", authPathItem);

            authOperation.addTagsItem("OAuth 2.0");
        };
    }
}
