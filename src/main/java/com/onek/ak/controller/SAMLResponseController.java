package com.onek.ak.controller;

import com.onek.ak.model.SamlConfiguration;
import com.onek.ak.service.SamlConfigurationService;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.impl.ResponseUnmarshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.*;

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
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.List;

@Controller
@RequestMapping("/saml")
public class SAMLResponseController {

    // Constant for default photo
    private static final String DEFAULT_PHOTO = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAABmJLR0QA/wD/AP+gvaeTAAACMUlEQVR4nO2ay0rDQBCG37X1KmqEokIoohAo/AOjkDp6SgfIGhKpaCWjgDkAYaCNfwCXQVAooKSrFSFpp2YS9/xrWxvlsPi7c2bm5gBuAzmTz/m8AHgA6ABf1xA1JQN0A7gOsA+oBRgAk3Uovd9dwGngbmcBlmMl9/KDeBp0Bfr4ghbBauAH8nIH7vWi4bgFHgR0lmZHZmoBf9zrlj3AK+BZ2zLWj3QEXoA3mAw/kCkgAeAbEDCZwGT8tsDDE8uoAjRmB7gvVNAC5il5tFkNDEOwFC0LSqAVoFX79aEUAm0AoqzkcYJggNkMBJ0+QMUxjbVVnAATm4sTImMm3IZMBlcwBYOrNcGiFgBxoC29yyMgS4FhsIDUb5ymNgMBoCkhNkApUBzS8FgDQcZthjAV3X2exBWWO0kkQT2YEdcGNCNu6eNskmQB3rH/gI4JrggpysIBUFoCMhM1mCQCtNqhQ6dwj4wOCPTyxkDI1QELR7NgBQNTRgTbQULgI3bq1f5+zZLCHbDM1MPv+XhHKAHK3bNo8XfjEQlsNdQDYxaUXy3XLgCZoECuAJc7IQFgvLu+lZaMALqWVLO6suoKoAkAEpP7hSKyAgBTp84wXr8g3LLv3OVvwsGeC9IG5yMYTJB4wAAAABJRU5ErkJggg==";

    @Autowired private SamlConfigurationService configService;

    @PostMapping("/acs")  // Assertion Consumer Service
    public String handleSAMLResponse(@RequestParam("SAMLResponse") String samlResponse, HttpSession session) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(samlResponse);
            String decodedXML = new String(decodedBytes);

            System.out.println("Decoded SAML Response: " + decodedXML);

            // Extract user details from SAML Response
            Map<String, Object> userDetails = extractUserDetails(decodedXML);

            // Store extracted fields in session with proper type handling
            session.setAttribute("firstname", getSessionValue(userDetails.get("firstname"), "Unknown"));
            session.setAttribute("lastname", getSessionValue(userDetails.get("lastname"), "Unknown"));
            session.setAttribute("email", getSessionValue(userDetails.get("email"), "Unknown"));
            session.setAttribute("role", getSessionValue(userDetails.get("role"), "Sales"));
            session.setAttribute("uid", getSessionValue(userDetails.get("uid"), "Unknown"));
            session.setAttribute("title", getSessionValue(userDetails.get("title"), ""));
            session.setAttribute("role", getSessionValue(userDetails.get("role"), "Marketing"));

            // Handle the photo attribute
            Object photoValue = userDetails.get("photo");
            if (photoValue instanceof List) {
                List<?> photoList = (List<?>) photoValue;
                session.setAttribute("thumbnailphoto", photoList.isEmpty() ?
                        DEFAULT_PHOTO : photoList.get(0));
            } else {
                session.setAttribute("thumbnailphoto",
                        photoValue != null ? photoValue : DEFAULT_PHOTO);
            }

            // Store any list-type attributes directly
            if (userDetails.containsKey("groups")) {
                session.setAttribute("groups", userDetails.get("groups"));
            }

            return "redirect:/dashboard.html";  // Redirect to dashboard
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error.html";
        }
    }

    // Helper function to safely get values with proper type handling
    private Object getSessionValue(Object value, Object defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        // If it's a list but we want a single value, take the first element
        if (value instanceof List && !((List<?>) value).isEmpty()) {
            return ((List<?>) value).get(0);
        }
        return value;
    }


    private Map<String, Object> extractUserDetails(String samlXML) {
        Map<String, Object> userDetails = new HashMap<>();
        try {
            // Get config with attribute type definitions
            SamlConfiguration config = configService.getConfiguration();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(samlXML.getBytes()));

            UnmarshallerFactory unmarshallerFactory = XMLObjectProviderRegistrySupport.getUnmarshallerFactory();
            Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
            Response response = (Response) unmarshaller.unmarshall(document.getDocumentElement());

            List<Assertion> assertions = response.getAssertions();
            if (!assertions.isEmpty()) {
                Assertion assertion = assertions.get(0);
                for (AttributeStatement attributeStatement : assertion.getAttributeStatements()) {
                    for (Attribute attribute : attributeStatement.getAttributes()) {
                        String attributeName = attribute.getName().toLowerCase();
                        List<XMLObject> attributeValues = attribute.getAttributeValues();

                        config.getAttributeMappings()
                                .stream()
                                .filter(attributeMap -> attributeMap.mappedName.equals(attributeName))
                                .findFirst()
                                .ifPresent(attributeMap -> {
                                    // Check if this should be treated as a list
                                    if (attributeMap.isList) {
                                        List<String> values = new ArrayList<>();
                                        for (XMLObject attributeValue : attributeValues) {
                                            values.add(attributeValue.getDOM().getTextContent().trim());
                                        }
                                        userDetails.put(attributeMap.attributeName, values);
                                    } else {
                                        // Single value (take first one)
                                        if (!attributeValues.isEmpty()) {
                                            userDetails.put(attributeMap.attributeName, attributeValues.get(0).getDOM().getTextContent().trim());
                                        }
                                    }
                                });
                        // Check if this should be treated as a list
//                        if (config.isAttributeList(attributeName)) {
//                            List<String> values = new ArrayList<>();
//                            for (XMLObject attributeValue : attributeValues) {
//                                values.add(attributeValue.getDOM().getTextContent().trim());
//                            }
//                            userDetails.put(attributeName, values);
//                        } else {
//                            // Single value (take first one)
//                            if (!attributeValues.isEmpty()) {
//                                userDetails.put(attributeName, attributeValues.get(0).getDOM().getTextContent().trim());
//                            }
//                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userDetails;
    }

//    private Map<String, String> extractUserDetails(String samlXML) {
//        Map<String, String> userDetails = new HashMap<>();
//        try {
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            factory.setNamespaceAware(true);
//            DocumentBuilder builder = factory.newDocumentBuilder();
//            org.w3c.dom.Document document = builder.parse(new java.io.ByteArrayInputStream(samlXML.getBytes()));
//
//            UnmarshallerFactory unmarshallerFactory = XMLObjectProviderRegistrySupport.getUnmarshallerFactory();
//            Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
//            Response response = (Response) unmarshaller.unmarshall(document.getDocumentElement());
//
//            List<Assertion> assertions = response.getAssertions();
//            if (!assertions.isEmpty()) {
//                Assertion assertion = assertions.get(0);
//                List<AttributeStatement> attributeStatements = assertion.getAttributeStatements();
//                for (AttributeStatement attributeStatement : attributeStatements) {
//                    for (Attribute attribute : attributeStatement.getAttributes()) {
//                        String attributeName = attribute.getName();
//                        List<XMLObject> attributeValues = attribute.getAttributeValues();
//                        if (!attributeValues.isEmpty()) {
//                            String value = attributeValues.get(0).getDOM().getTextContent();
//                            userDetails.put(attributeName.toLowerCase(), value);
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return userDetails;
//    }

//    private Map<String, List<String>> extractUserDetails(String samlXML) {
//        Map<String, List<String>> userDetails = new HashMap<>();
//        try {
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            factory.setNamespaceAware(true);
//            DocumentBuilder builder = factory.newDocumentBuilder();
//            org.w3c.dom.Document document = builder.parse(new java.io.ByteArrayInputStream(samlXML.getBytes()));
//
//            UnmarshallerFactory unmarshallerFactory = XMLObjectProviderRegistrySupport.getUnmarshallerFactory();
//            Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
//            Response response = (Response) unmarshaller.unmarshall(document.getDocumentElement());
//
//            List<Assertion> assertions = response.getAssertions();
//            if (!assertions.isEmpty()) {
//                Assertion assertion = assertions.get(0);
//                List<AttributeStatement> attributeStatements = assertion.getAttributeStatements();
//                for (AttributeStatement attributeStatement : attributeStatements) {
//                    for (Attribute attribute : attributeStatement.getAttributes()) {
//                        String attributeName = attribute.getName();
//                        List<XMLObject> attributeValues = attribute.getAttributeValues();
//
//                        // Create a list to hold all values for this attribute
//                        List<String> values = new ArrayList<>();
//
//                        for (XMLObject attributeValue : attributeValues) {
//                            String value = attributeValue.getDOM().getTextContent().trim();
//                            values.add(value);
//                        }
//
//                        if (!values.isEmpty()) {
//                            userDetails.put(attributeName.toLowerCase(), values);
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return userDetails;
//    }
}
