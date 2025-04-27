package com.onek.ak.model;


import java.util.List;
import java.util.Map;

public class SamlConfiguration {
    private String spEntityId;
    private String assertionConsumerServiceURL;
    private String idpSSOUrl;
    private List<AttributeMap> attributeMappings;
    //create getters and setters for attributeMappings

    public List<AttributeMap> getAttributeMappings() {
        return attributeMappings;
    }

    public void setAttributeMappings(List<AttributeMap> attributeMappings) {
        this.attributeMappings = attributeMappings;
    }

    // Constructors, getters, setters
    public SamlConfiguration() {}

    public SamlConfiguration(String spEntityId, String assertionConsumerServiceURL,
                             String idpSSOUrl) {
        this.spEntityId = spEntityId;
        this.assertionConsumerServiceURL = assertionConsumerServiceURL;
        this.idpSSOUrl = idpSSOUrl;
    }

    // Getters and Setters
    public String getSpEntityId() { return spEntityId; }
    public void setSpEntityId(String spEntityId) { this.spEntityId = spEntityId; }
    public String getAssertionConsumerServiceURL() { return assertionConsumerServiceURL; }
    public void setAssertionConsumerServiceURL(String assertionConsumerServiceURL) { this.assertionConsumerServiceURL = assertionConsumerServiceURL; }
    public String getIdpSSOUrl() { return idpSSOUrl; }
    public void setIdpSSOUrl(String idpSSOUrl) { this.idpSSOUrl = idpSSOUrl; }

    public boolean isValid() {
        return isNotBlank(spEntityId)
                && isNotBlank(assertionConsumerServiceURL)
                && isNotBlank(idpSSOUrl);
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "SamlConfiguration{" +
                "spEntityId='" + spEntityId + '\'' +
                ", assertionConsumerServiceURL='" + assertionConsumerServiceURL + '\'' +
                ", idpSSOUrl='" + idpSSOUrl + '\'' +
                '}';
    }
}
