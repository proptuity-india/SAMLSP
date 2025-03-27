package com.onek.ak.repository;

import com.onek.ak.model.SamlConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SamlConfigurationRepository extends MongoRepository<SamlConfiguration, String> {
    // We'll only have one configuration, so we can use this method
    SamlConfiguration findFirstByOrderByIdAsc();
}