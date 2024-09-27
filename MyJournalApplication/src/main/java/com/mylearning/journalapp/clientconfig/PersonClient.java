package com.mylearning.journalapp.clientconfig;

import com.mylearning.journalapp.clientresponse.Person;
import com.mylearning.journalapp.clientresponse.PersonResource;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import java.util.List;


public interface PersonClient {

    @GetExchange("/oldestPerson")
    List<Document> getOldestPersonByCityExchange();

    @GetExchange("/populationByCity")
    List<Document> getPopulationByCityExchange();

    @GetExchange("/{name}/{age}")
    Person getPersonByNameAndAgePathVariableExchange(@PathVariable String name, @PathVariable Integer age);

    @PostExchange("/create-person-on-status")
    Person createPersonExchange(@RequestBody Person person);

    @PutExchange
    Person updatePersonByExchange(@RequestBody Person person, @RequestParam ObjectId personId);

    @DeleteExchange("/{personId}")
    void deletePersonByExchange(@PathVariable ObjectId personId);

    @GetExchange("/search")
    Object searchPersonByExchange(
            @RequestParam(required = false) String name,
            @RequestParam(value = "min",required = false) Integer minAge,
            @RequestParam(value = "max",required = false) Integer maxAge,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size
    );

    @GetExchange("/all-person")
    List<PersonResource> getAllPersonsByExchange();
}
