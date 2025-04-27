package com.onek.ak.service;

import com.onek.ak.model.AttributeMap;
import com.onek.ak.model.SamlConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SamlConfigurationService {

    @Value("${saml.sp.entity-id}") private String spEntityId;
    @Value("${saml.sp.acs-url}") private String assertionConsumerServiceURL;
    @Value("${saml.idp.sso-url}") private String idpSSOUrl;
    @Value("${saml.auth-contextVal}") private String authCtx;

    private SamlConfiguration config = new SamlConfiguration();

    public SamlConfiguration getConfiguration() {
        if(this.config.isValid()){
            return this.config;
        }
        System.out.println("Creating new configuration using application.properties values");
        this.config = new SamlConfiguration(spEntityId, assertionConsumerServiceURL, idpSSOUrl);

        if (this.config.getAttributeMappings() == null || this.config.getAttributeMappings().isEmpty()) {
            this.config.setAttributeMappings(List.of(
                    new AttributeMap("email", "email", false),
                    new AttributeMap("firstname", "firstname", false),
                    new AttributeMap("lastname", "lastname", false),
                    new AttributeMap("role", "role", false),
                    new AttributeMap("uid", "uid", false),
                    new AttributeMap("title", "title", false)
            ));
        }

        return this.config;
    }

    public SamlConfiguration saveConfiguration(SamlConfiguration config) {
        if(config.isValid()){
            this.config = config;
        }
        return this.config;
    }
}