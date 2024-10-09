package com.mylearning.journalapp.clientdynamicproperty;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DynamicPropertyService {
    private final DynamicConfigProperties configProperties;

    public DynamicPropertyService(DynamicConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    public String getType() {
        return configProperties.getType();
    }

    public void updateType(String newType) {
        configProperties.setType(newType);
    }
}
