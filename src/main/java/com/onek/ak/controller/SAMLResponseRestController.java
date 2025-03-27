package com.onek.ak.controller;


import com.onek.ak.model.SAMLResponseModel;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.StringReader;
import java.util.Base64;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SAMLResponseRestController {

    @PostMapping("/acs")
    public SAMLResponseModel samlResponse(@RequestParam("SAMLResponse") String samlResponse) {
        try {
            System.out.println("Received SAML Response (Base64): " + samlResponse);

            // Decode Base64 SAML response
            byte[] decodedBytes = Base64.getDecoder().decode(samlResponse);
            String decodedResponse = new String(decodedBytes);
            System.out.println("Decoded SAML Response: " + decodedResponse);

            // Parse XML into Java object
            JAXBContext jaxbContext = JAXBContext.newInstance(SAMLResponseModel.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            SAMLResponseModel responseModel = (SAMLResponseModel) unmarshaller.unmarshal(new StringReader(decodedResponse));

            return responseModel;
        } catch (Exception e) {
            throw new RuntimeException("Error processing SAML Response: " + e.getMessage(), e);
        }
    }
}
