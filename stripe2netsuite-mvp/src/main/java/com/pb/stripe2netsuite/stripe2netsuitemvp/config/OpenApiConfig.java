package com.pb.stripe2netsuite.stripe2netsuitemvp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (SpringDoc) configuration for API documentation.
 * 
 * Access the API documentation at:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8080/v3/api-docs
 * - OpenAPI YAML: http://localhost:8080/v3/api-docs.yaml
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Stripe to NetSuite Integration API")
                        .description("""
                                This API receives Stripe webhook events and synchronizes them to NetSuite.
                                
                                ## Flow
                                1. Stripe sends webhook events to `/webhook/stripe`
                                2. Events are verified and parsed
                                3. Events are processed and synced to NetSuite
                                
                                ## Supported Events
                                - `payment_intent.succeeded` - Payment successful
                                - `payment_intent.payment_failed` - Payment failed
                                - `charge.succeeded` - Charge successful
                                - `charge.failed` - Charge failed
                                - `invoice.paid` - Invoice paid
                                - `invoice.payment_failed` - Invoice payment failed
                                - `customer.subscription.created` - Subscription created
                                - `customer.subscription.updated` - Subscription updated
                                - `customer.subscription.deleted` - Subscription cancelled
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Development Team")
                                .email("dev@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token for API authentication (if required)"))
                        .addSecuritySchemes("stripe-signature", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("Stripe-Signature")
                                .description("Stripe webhook signature for verification")));
    }
}
