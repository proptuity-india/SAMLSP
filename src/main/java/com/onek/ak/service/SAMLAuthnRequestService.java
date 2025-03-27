package com.onek.ak.service;


import org.opensaml.saml.saml2.core.*;
import org.opensaml.saml.saml2.metadata.*;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.xmlsec.signature.SignableXMLObject;
import org.springframework.stereotype.Service;

@Service
public class SAMLAuthnRequestService {

    private final XMLObjectBuilderFactory builderFactory;

    public SAMLAuthnRequestService() {
        this.builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
    }

    public AuthnRequest createAuthnRequest(String idpSSOUrl, String spEntityId, String assertionConsumerServiceUrl) {
        AuthnRequest authnRequest = (AuthnRequest) builderFactory.getBuilder(AuthnRequest.DEFAULT_ELEMENT_NAME)
                .buildObject(AuthnRequest.DEFAULT_ELEMENT_NAME);

        authnRequest.setDestination(idpSSOUrl);
        authnRequest.setIssuer(createIssuer(spEntityId));
        authnRequest.setAssertionConsumerServiceURL(assertionConsumerServiceUrl);
        authnRequest.setID("_" + System.currentTimeMillis()); // Unique ID

        return authnRequest;
    }

    private Issuer createIssuer(String spEntityId) {
        Issuer issuer = (Issuer) builderFactory.getBuilder(Issuer.DEFAULT_ELEMENT_NAME)
                .buildObject(Issuer.DEFAULT_ELEMENT_NAME);
        issuer.setValue(spEntityId);
        return issuer;
    }
}
