package com.mylearning.journalapp.clientopenfeign;

import com.mylearning.journalapp.clientresponse.JWTAuthResponse;
import com.mylearning.journalapp.clientresponse.LoginDto;
import com.mylearning.journalapp.clientresponse.Person;
import com.mylearning.journalapp.clientresponse.PersonResource;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import java.util.List;

/**
 * just like value and url we can also add another attribute i.e.  configuration = FeignClientConfig.class
 *  To inject all these beans (encoders, decoders, interceptors, etc.) into your Feign client, you need to
 *  specify the configuration class in the Feign client interface. Feign clients can be configured with
 *  a custom configuration class using the @FeignClient annotation,
 *
 *  like this ::
 * @FeignClient(name = "personFeignClient", url = "http://localhost:8080", configuration = FeignClientConfig.class)
 *   configuration = FeignConfig.class
 *   @RequestHeader("Authorization") String jwtToken
 */
@FeignClient(value = "PERSON-FEIGN-CLIENT", url = "http://localhost:8081/api/person", configuration = FeignConfig.class)
public interface PersonFeignClient {

    // later separated this abstract method to avoid confusion so to use @RequestHeader we created another FeignClient Interface and used this method in another
    /*@GetMapping(value = "/populationByCity")
    ResponseEntity<List<Document>> getPopulationByCity(@RequestHeader("Authorization") String jwtToken);*/

    @GetMapping(value = "/oldestPerson")
    ResponseEntity<List<Document>> getOldestPersonByCity();

    @GetMapping("/{name}/{age}")
    ResponseEntity<Person> getPersonByNameAndAgePathVariableExchange(@PathVariable String name, @PathVariable Integer age);

    @PostMapping("/create-person-on-status")
    ResponseEntity<Person> createPersonExchange(@RequestBody Person person);

    @PutMapping
    ResponseEntity<Person> updatePersonByExchange(@RequestBody Person person, @RequestParam ObjectId personId);

    @DeleteMapping("/{personId}")
    ResponseEntity<Void> deletePersonByExchange(@PathVariable ObjectId personId);

    @GetMapping("/search")
    Object searchPersonByExchange(
            @RequestParam(required = false) String name,
            @RequestParam(value = "min",required = false) Integer minAge,
            @RequestParam(value = "max",required = false) Integer maxAge,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size
    );

    @GetMapping("/all-person")
    List<PersonResource> getAllPersonsByExchange();

}
