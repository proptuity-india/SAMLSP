package com.onek.ak.service;

import com.onek.ak.model.SamlConfiguration;
import com.onek.ak.repository.SamlConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SamlConfigurationService {

    private final SamlConfigurationRepository repository;

    @Autowired
    public SamlConfigurationService(SamlConfigurationRepository repository) {
        this.repository = repository;
    }

    public SamlConfiguration getConfiguration() {
        return repository.findFirstByOrderByIdAsc();
    }

    public SamlConfiguration saveConfiguration(SamlConfiguration config) {
        // Delete any existing configuration first
        repository.deleteAll();
        return repository.save(config);
    }
}