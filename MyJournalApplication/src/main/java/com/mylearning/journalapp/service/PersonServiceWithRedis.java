package com.mylearning.journalapp.service;

import com.mylearning.journalapp.clientresponse.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PersonServiceWithRedis {
    private final RestClient restClient;

    private final RedisService redisService;
    @Autowired
    public PersonServiceWithRedis(RestClient restClient, RedisService redisService) {
        this.restClient = restClient;
        this.redisService = redisService;
    }

    public Person getPersonByNameAndAgePathVariable(String firstName, Integer age){

        Map<String,String> mapVal = new HashMap<>();
        mapVal.put("name",firstName);
        mapVal.put("age",String.valueOf(age));

        Person personResponse = redisService.get("personByNameAge", Person.class);

        if(personResponse != null) {
            return personResponse;
        }else {
            personResponse = restClient.get()
                    .uri("/{name}/{age}", mapVal)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(Person.class);

            if(personResponse!=null) redisService.set("personByNameAge", personResponse,120000l);

            return personResponse;
        }
    }
}
