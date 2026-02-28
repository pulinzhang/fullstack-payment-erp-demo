package com.pb.stripe2netsuite.stripe2netsuitemvp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for NetSuite integration.
 * 
 * To get these credentials:
 * 1. Go to NetSuite > Integration > Integration Management
 * 2. Create a new integration with "Token-Based Authentication" enabled
 * 3. Go to Users > Access Tokens > New Access Token
 * 4. Create token for your integration
 */
@Configuration
@ConfigurationProperties(prefix = "netsuite")
public class NetSuiteConfig {

    /**
     * NetSuite Account ID.
     * Found in Setup > Company > Company Information > Account ID
     * Example: 1234567 or TSTDRV123456
     */
    private String accountId = "";

    /**
     * Consumer Key from NetSuite integration.
     */
    private String consumerKey = "";

    /**
     * Consumer Secret from NetSuite integration.
     */
    private String consumerSecret = "";

    /**
     * Token ID from NetSuite access token.
     */
    private String tokenId = "";

    /**
     * Token Secret from NetSuite access token.
     */
    private String tokenSecret = "";

    /**
     * NetSuite REST API base URL.
     * Production: https://{accountId}.suitetalk.api.netsuite.com
     * Sandbox: https://{accountId}.suitetalk.api.netsuite.com
     */
    private String baseUrl = "";

    /**
     * RESTlet script ID for custom endpoints.
     */
    private String restletScriptId = "";

    /**
     * RESTlet deployment ID.
     */
    private String restletDeploymentId = "";

    // Getters and Setters

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getRestletScriptId() {
        return restletScriptId;
    }

    public void setRestletScriptId(String restletScriptId) {
        this.restletScriptId = restletScriptId;
    }

    public String getRestletDeploymentId() {
        return restletDeploymentId;
    }

    public void setRestletDeploymentId(String restletDeploymentId) {
        this.restletDeploymentId = restletDeploymentId;
    }

    /**
     * Check if NetSuite integration is configured.
     */
    public boolean isConfigured() {
        return accountId != null && !accountId.isEmpty()
            && consumerKey != null && !consumerKey.isEmpty()
            && tokenId != null && !tokenId.isEmpty();
    }
}

