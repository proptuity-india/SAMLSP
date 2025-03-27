package com.onek.ak.controller;

import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.impl.ResponseUnmarshaller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.List;
import jakarta.servlet.http.HttpSession;

import jakarta.servlet.http.HttpSession;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.*;
import org.opensaml.saml.saml2.core.impl.ResponseUnmarshaller;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallerFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/saml")
public class SAMLResponseController {

    @PostMapping("/acs")  // Assertion Consumer Service
    public String handleSAMLResponse(@RequestParam("SAMLResponse") String samlResponse, HttpSession session) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(samlResponse);
            String decodedXML = new String(decodedBytes);

            System.out.println("Decoded SAML Response: " + decodedXML);

            // Extract user details from SAML Response
            Map<String, String> userDetails = extractUserDetails(decodedXML);



            // Store extracted fields in session
            session.setAttribute("firstname", userDetails.getOrDefault("firstname", "Unknown"));
            session.setAttribute("lastname", userDetails.getOrDefault("lastname", "Unknown"));
            session.setAttribute("email", userDetails.getOrDefault("email", "Unknown"));
            session.setAttribute("role", userDetails.getOrDefault("role", "Sales"));

            return "redirect:/dashboard.html";  // Redirect to dashboard
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error.html";
        }
    }

    private Map<String, String> extractUserDetails(String samlXML) {
        Map<String, String> userDetails = new HashMap<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document document = builder.parse(new java.io.ByteArrayInputStream(samlXML.getBytes()));

            UnmarshallerFactory unmarshallerFactory = XMLObjectProviderRegistrySupport.getUnmarshallerFactory();
            Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
            Response response = (Response) unmarshaller.unmarshall(document.getDocumentElement());

            List<Assertion> assertions = response.getAssertions();
            if (!assertions.isEmpty()) {
                Assertion assertion = assertions.get(0);
                List<AttributeStatement> attributeStatements = assertion.getAttributeStatements();
                for (AttributeStatement attributeStatement : attributeStatements) {
                    for (Attribute attribute : attributeStatement.getAttributes()) {
                        String attributeName = attribute.getName();
                        List<XMLObject> attributeValues = attribute.getAttributeValues();
                        if (!attributeValues.isEmpty()) {
                            String value = attributeValues.get(0).getDOM().getTextContent();
                            userDetails.put(attributeName.toLowerCase(), value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userDetails;
    }
}
