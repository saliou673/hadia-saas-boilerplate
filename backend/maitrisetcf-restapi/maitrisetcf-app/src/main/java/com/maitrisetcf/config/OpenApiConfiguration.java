package com.maitrisetcf.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
    @Bean
    public OpenAPI fullOpenAPIConfig(@Value("${app.version}") String version,
                                     @Value("${app.description}") String description) {
        Info info = new Info()
                .title("E-Timbre REST API")
                .version(description)
                .description(version)
                .termsOfService("https://maitrisetcf.com/terms/")
                .license(new License().name("Copyright").url("https://maitrisetcf.com"));

        return new OpenAPI().info(info);
    }
}
