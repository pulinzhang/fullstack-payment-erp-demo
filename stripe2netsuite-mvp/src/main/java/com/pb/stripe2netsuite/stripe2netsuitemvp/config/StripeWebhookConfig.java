package com.pb.stripe2netsuite.stripe2netsuitemvp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for Stripe webhook.
 */
@Configuration
@ConfigurationProperties(prefix = "stripe.webhook")
public class StripeWebhookConfig {

    /**
     * Stripe webhook signing secret.
     * Configure this in application.properties: stripe.webhook.secret=whsec_...
     */
    private String secret = "";

    /**
     * Flag to enable/disable signature verification.
     * Set to false for testing only (not recommended for production).
     */
    private boolean verifySignature = true;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public boolean isVerifySignature() {
        return verifySignature;
    }

    public void setVerifySignature(boolean verifySignature) {
        this.verifySignature = verifySignature;
    }
}

