package com.pb.stripe2netsuite.stripe2netsuitemvp.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for Stripe API.
 */
@Configuration
@ConfigurationProperties(prefix = "stripe")
public class StripeConfig {

    /**
     * Stripe secret API key.
     * Configure in application.properties: stripe.api.key=sk_test_...
     */
    private String apiKey = "";

    /**
     * Stripe publishable key (for frontend).
     * Configure in application.properties: stripe.publishable.key=pk_test_...
     */
    private String publishableKey = "";

    @PostConstruct
    public void init() {
        if (apiKey != null && !apiKey.isEmpty()) {
            Stripe.apiKey = apiKey;
        }
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getPublishableKey() {
        return publishableKey;
    }

    public void setPublishableKey(String publishableKey) {
        this.publishableKey = publishableKey;
    }
}
