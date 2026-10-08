package com.heiguang.portfolio.inventory.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI inventoryOpenApi() {
        return new OpenAPI().info(new Info()
                .title("E-commerce Inventory API")
                .version("1.0.0")
                .description("Portfolio demo for product and inventory management with validation and optimistic locking")
                .contact(new Contact().name("Guang H.")));
    }
}
