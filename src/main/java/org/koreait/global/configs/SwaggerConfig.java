package org.koreait.global.configs;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
info = @Info(title = "회원 인증/인가 API",
             description = "회원 가입 및 로그인, 회원 인가 체크")
)
@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi openApiGroup() {
        return GroupedOpenApi.builder()
                .group("회원 인증/인가 API V1")
                .pathsToMatch("/**") // 문서의 범위
                .build();
    }
}
