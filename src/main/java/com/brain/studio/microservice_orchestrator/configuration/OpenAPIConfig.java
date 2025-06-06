package com.brain.studio.microservice_orchestrator.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI microserviceOrchestrationAPI(){
        return new OpenAPI()
                .info(new Info().title("Microservice Orchestrator API")
                        .description("Mono2Micro support for microservice generation")
                        .version("v0.0.1"));
    }
}
