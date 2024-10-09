package com.mylearning.journalapp.clientdynamicproperty;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 *
 * # Can be 'rest-client', 'rest-template', or 'web-client'
 * person.client.type=rest-client
 *
 * With this setup, we can dynamically change the value of person.client.type at runtime by calling the controller endpoint.
 *
 * Example API Calls:
 *
 * Get Current Type: GET http://localhost:6969/api/dynamic/properties/type
 * Update Type: POST http://localhost:6969/api/dynamic/properties/type?newType=rest-client
 *
 */
@RestController
@RequestMapping("/api/dynamic/properties")
@Slf4j
public class DynamicPropertyController {
    private final DynamicPropertyService dynamicPropertyService;

    public DynamicPropertyController(DynamicPropertyService dynamicPropertyService) {
        this.dynamicPropertyService = dynamicPropertyService;
    }

    @GetMapping("/type")
    public String getType() {
        return dynamicPropertyService.getType();
    }

    @PostMapping("/type")
    public String updateType(@RequestParam String newType) {
        dynamicPropertyService.updateType(newType);
        return "Message updated to: " + newType;
    }
}
