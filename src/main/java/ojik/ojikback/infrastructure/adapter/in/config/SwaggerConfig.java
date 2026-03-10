package ojik.ojikback.infrastructure.adapter.in.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI ojikOpenApi() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes(
                        "bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .name("Authorization")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .info(new Info()
                        .title("OJik API")
                        .version("v1")
                        .description("OJik Backend API 문서")
                        .contact(new Contact().name("Ojik Team")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/**")
                .build();
    }
}
