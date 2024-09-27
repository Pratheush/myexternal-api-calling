package com.mylearning.journalapp.clientopenfeign;

import com.mylearning.journalapp.clientresponse.Person;
import feign.Feign;
import feign.gson.GsonDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/decorate-with feignbuilder-decoder")
@Slf4j
public class PersonFeignClientByNameAgeController {

    private final PersonFeignClientByNameAGe personClientByNameAge;

    //@Autowired
    public PersonFeignClientByNameAgeController() {
        this.personClientByNameAge = Feign.builder()
                .decoder(new GsonDecoder())
                .target(PersonFeignClientByNameAGe.class, "http://localhost:8081/api/person");
    }

    @GetMapping(value = "/{name}/{age}")
    public Person getPersonByNameAndAgePathVariableExchange(@PathVariable String name, @PathVariable Integer age) {
        return personClientByNameAge.getPersonByNameAndAgePathVariableExchange(name,age);
    }
}
