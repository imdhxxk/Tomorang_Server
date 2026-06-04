package kr.hs.after.Tomorang.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${app.server-url:http://localhost:8081}")
    private String serverUrl;

    @Bean
    public OpenAPI openAPI() {
        final String jwtScheme = "BearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Tomorang API")
                        .description("개인 관광 가이드 플랫폼 — 투어 게시물, 채팅, 회원 관리 API")
                        .version("v1.0.0"))
                .servers(List.of(new Server().url(serverUrl)))
                .addSecurityItem(new SecurityRequirement().addList(jwtScheme))
                .components(new Components()
                        .addSecuritySchemes(jwtScheme, new SecurityScheme()
                                .name(jwtScheme)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("로그인 후 발급된 JWT 토큰을 입력하세요 (Bearer 생략)")));
    }
}
