package com.freightfox.dispatch.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8081}")
    private String serverPort;

    @Bean
    public OpenAPI dispatchOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FreightFox - Dispatch Load Balancer API")
                        .description("Production-grade Spring Boot REST API for optimizing vehicle fleet dispatch. " +
                                "Allocates delivery orders based on location proximity (Haversine formula), " +
                                "package weight capacity limits, and priority scheduling (HIGH -> MEDIUM -> LOW).")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Shubhash Yadav")
                                .email("shubhashyadav@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server().url("/").description("Current Server (Local or Live Deployed)")
                ));
    }
}
