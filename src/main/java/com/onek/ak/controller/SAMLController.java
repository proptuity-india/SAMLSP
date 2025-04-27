package com.onek.ak.controller;


import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import com.onek.ak.model.SamlConfiguration;
import com.onek.ak.service.SamlConfigurationService;
import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml.saml2.core.impl.AuthnContextClassRefBuilder;
import org.opensaml.saml.saml2.core.impl.AuthnRequestBuilder;
import org.opensaml.saml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml.saml2.core.impl.NameIDPolicyBuilder;
import org.opensaml.saml.saml2.core.impl.RequestedAuthnContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Element;

import net.shibboleth.utilities.java.support.xml.SerializeSupport;

@RestController
@RequestMapping("/saml")
public class SAMLController {

    @Value("${saml.sp.entity-id}") private String spEntityId;
    @Value("${saml.sp.acs-url}") private String assertionConsumerServiceURL;
    @Value("${saml.idp.sso-url}") private String idpSSOUrl;
    @Value("${saml.auth-contextVal}") private String authCtx;

    private final SamlConfigurationService configService;

    @Autowired
    public SAMLController(SamlConfigurationService configService) {
        this.configService = configService;
    }

    @GetMapping("/login")
    public ResponseEntity<Void> samlLogin() {

        var config = configService.getConfiguration();

        try {
            // Create AuthnRequest using values from MongoDB
            AuthnRequest authnRequest = new AuthnRequestBuilder().buildObject();
            authnRequest.setID("ID-" + System.currentTimeMillis());
            authnRequest.setIssueInstant(Instant.now());
            authnRequest.setDestination(config.getIdpSSOUrl());
            authnRequest.setProtocolBinding("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST");
            authnRequest.setForceAuthn(true);

            // Set Issuer
            Issuer issuer = new IssuerBuilder().buildObject();
//            issuer.setValue(config.getSpEntityId());
            issuer.setValue(config.getSpEntityId());
            authnRequest.setIssuer(issuer);

            // Set NameID Policy
            NameIDPolicy nameIDPolicy = new NameIDPolicyBuilder().buildObject();
            nameIDPolicy.setFormat("urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress");
            authnRequest.setNameIDPolicy(nameIDPolicy);

            // Add RequestedAuthnContext
            RequestedAuthnContext requestedAuthnContext = new RequestedAuthnContextBuilder().buildObject();
            requestedAuthnContext.setComparison(AuthnContextComparisonTypeEnumeration.EXACT);

            AuthnContextClassRef authnContextClassRef = new AuthnContextClassRefBuilder().buildObject();
            authnContextClassRef.setURI(authCtx);
            requestedAuthnContext.getAuthnContextClassRefs().add(authnContextClassRef);
            authnRequest.setRequestedAuthnContext(requestedAuthnContext);

            // Convert SAML AuthnRequest to XML
            MarshallerFactory marshallerFactory = XMLObjectProviderRegistrySupport.getMarshallerFactory();
            Marshaller marshaller = marshallerFactory.getMarshaller(authnRequest);
            Element authnRequestElement = marshaller.marshall(authnRequest);
            String authnRequestXML = SerializeSupport.nodeToString(authnRequestElement);

            // Compress and encode
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(
                    byteArrayOutputStream, new Deflater(Deflater.DEFAULT_COMPRESSION, true));
            deflaterOutputStream.write(authnRequestXML.getBytes(StandardCharsets.UTF_8));
            deflaterOutputStream.close();

            String encodedRequest = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
            String urlEncodedRequest = URLEncoder.encode(encodedRequest, StandardCharsets.UTF_8);

            // Redirect to IdP
            String redirectURL = config.getIdpSSOUrl() + "?SAMLRequest=" + urlEncodedRequest;

            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(redirectURL));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
