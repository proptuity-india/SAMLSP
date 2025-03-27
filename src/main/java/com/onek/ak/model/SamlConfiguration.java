package com.onek.ak.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "saml_configurations")
public class SamlConfiguration {
    @Id
    private String id;
    private String spEntityId;
    private String assertionConsumerServiceURL;
    private String idpSSOUrl;
    private String authContextValue;

    // Constructors, getters, setters
    public SamlConfiguration() {}

    public SamlConfiguration(String spEntityId, String assertionConsumerServiceURL,
                             String idpSSOUrl, String authContextValue) {
        this.spEntityId = spEntityId;
        this.assertionConsumerServiceURL = assertionConsumerServiceURL;
        this.idpSSOUrl = idpSSOUrl;
        this.authContextValue = authContextValue;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSpEntityId() { return spEntityId; }
    public void setSpEntityId(String spEntityId) { this.spEntityId = spEntityId; }
    public String getAssertionConsumerServiceURL() { return assertionConsumerServiceURL; }
    public void setAssertionConsumerServiceURL(String assertionConsumerServiceURL) { this.assertionConsumerServiceURL = assertionConsumerServiceURL; }
    public String getIdpSSOUrl() { return idpSSOUrl; }
    public void setIdpSSOUrl(String idpSSOUrl) { this.idpSSOUrl = idpSSOUrl; }
    public String getAuthContextValue() { return authContextValue; }
    public void setAuthContextValue(String authContextValue) { this.authContextValue = authContextValue; }
}
