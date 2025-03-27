package com.onek.ak.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetadataController {

    @GetMapping("/saml/metadata")
    public String getSPMetadata() {
        return "<EntityDescriptor entityID='http://localhost:8080/saml/metadata' " +
                "xmlns='urn:oasis:names:tc:SAML:2.0:metadata'>" +
                "<SPSSODescriptor>" +
                "<AssertionConsumerService Location='http://localhost:8080/saml/acs' Binding='urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST'/>" +
                "</SPSSODescriptor>" +
                "</EntityDescriptor>";
    }
}
