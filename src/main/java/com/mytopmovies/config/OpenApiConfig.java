package com.mytopmovies.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI myTopMoviesOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("MyTopMovies API")
                        .description("""
                                REST API for MyTopMovies: movie categories (New, Popular,
                                Best for Friends, by genre), JWT registration/login.

                                How to test protected endpoints:
                                1. Run POST /api/v1/auth/register or /login
                                2. Copy the accessToken from the response
                                3. Click the "Authorize" button at the top of the page
                                4. Paste just the token itself (without the word "Bearer" — it's added automatically)
                                """)
                        .version("v0.1")
                        .contact(new Contact().name("MyTopMovies backend team")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME_NAME, new SecurityScheme()
                                .name(BEARER_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}