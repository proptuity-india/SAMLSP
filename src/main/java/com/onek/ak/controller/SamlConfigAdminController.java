package com.onek.ak.controller;

import com.onek.ak.model.SamlConfiguration;
import com.onek.ak.service.SamlConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/saml-config")
public class SamlConfigAdminController {

    private final SamlConfigurationService configService;

    @Autowired
    public SamlConfigAdminController(SamlConfigurationService configService) {
        this.configService = configService;
    }

    @GetMapping
    public ResponseEntity<SamlConfiguration> getConfig() {
        SamlConfiguration config = configService.getConfiguration();
        if (config == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(config);
    }

    @PostMapping
    public ResponseEntity<SamlConfiguration> saveConfig(@RequestBody SamlConfiguration config) {
        SamlConfiguration savedConfig = configService.saveConfiguration(config);
        return ResponseEntity.ok(savedConfig);
    }
}