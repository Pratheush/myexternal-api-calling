package com.mylearning.journalapp.clientdynamicproperty;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "person.client")
@Getter
@Setter
public class DynamicConfigProperties {
    private String type;
}
