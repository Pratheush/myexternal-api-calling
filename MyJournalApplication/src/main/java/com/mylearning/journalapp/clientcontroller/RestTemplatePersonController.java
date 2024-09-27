package com.mylearning.journalapp.clientcontroller;

import com.mylearning.journalapp.client.RestTemplateCodeBufferPersonClient;
import com.mylearning.journalapp.clientresponse.Person;
import com.mylearning.journalapp.clientresponse.PersonPageResponse;
import com.mylearning.journalapp.clientresponse.PersonResource;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;

import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest-person")
@Slf4j
public class RestTemplatePersonController {

    private final RestTemplateCodeBufferPersonClient restTemplatePersonClient;

    public RestTemplatePersonController(RestTemplateCodeBufferPersonClient restTemplatePersonClient) {
        this.restTemplatePersonClient = restTemplatePersonClient;
    }

    @GetMapping("/population-by-city")
    public ResponseEntity<List<Document>> getPopulationByCity(){
        List<Document> documentList = restTemplatePersonClient.getPopulationByCity();
        return ResponseEntity.ok(documentList);
    }

    @GetMapping(value = "/oldest-person-by-city")
    public ResponseEntity<List<Document>> getOldestPersonByCity(){
        List<Document> documentList = restTemplatePersonClient.getOldestPersonByCity();
        return ResponseEntity.ok(documentList);
    }

    @GetMapping("/get-for-entity/{name}/{age}")
    public ResponseEntity<Person> getPersonByNameAndAgePathVariableGetForEntity(@PathVariable String name,@PathVariable Integer age){
        log.info("getPersonByNameAndAgePathVariableGetForEntity() called");
        Person person = restTemplatePersonClient.getPersonByNameAndAgePathVariableGetForEntity(name,age);
        return ResponseEntity.ok(person);
    }

    @GetMapping("/get-for-object/{name}/{age}")
    public ResponseEntity<Person> getPersonByNameAndAgePathVariableGetForObject(@PathVariable String name,@PathVariable Integer age){
        log.info("getPersonByNameAndAgePathVariableGetForObject() called");
        Person person = restTemplatePersonClient.getPersonByNameAndAgePathVariableGetForObject(name,age);
        return ResponseEntity.ok(person);
    }

    @GetMapping("/exchange/{name}/{age}")
    public ResponseEntity<Person> getPersonByNameAndAgePathVariableExchange(@PathVariable String name,@PathVariable Integer age){
        log.info("getPersonByNameAndAgePathVariableExchange() called");
        Person person = restTemplatePersonClient.getPersonByNameAndAgePathVariableGetForExchange(name,age);
        return ResponseEntity.ok(person);
    }

    @GetMapping("/execute/{name}/{age}")
    public ResponseEntity<Person> getPersonByNameAndAgePathVariableGetForExecute(@PathVariable String name,@PathVariable Integer age){
        log.info("getPersonByNameAndAgePathVariableGetForExecute() called");
        Person person = restTemplatePersonClient.getPersonByNameAndAgePathVariableGetForExecute(name,age);
        return ResponseEntity.ok(person);
    }

    @PostMapping("/post-for-object")
    public ResponseEntity<ObjectId> savePersonPostForObject(@RequestBody Person person){
        log.info("savePersonPostForObject() called");
        ObjectId objectId = restTemplatePersonClient.savePersonPostForObject(person);
        return ResponseEntity.status(HttpStatus.CREATED).body(objectId);
    }

    @PostMapping("/post-for-location")
    public ResponseEntity<URI> savePersonPostForLocation(@RequestBody Person person){
        log.info("savePersonPostForLocation() called");
        URI uriLocation = restTemplatePersonClient.savePersonPostForLocation(person);
        return ResponseEntity.status(HttpStatus.CREATED).body(uriLocation);
    }

    @PostMapping("/post-for-entity")
    public ResponseEntity<ObjectId> savePersonPostForEntity(@RequestBody Person person){
        log.info("savePersonPostForEntity() called");
        ObjectId id = restTemplatePersonClient.savePersonPostForEntity(person);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PutMapping
    public ResponseEntity<Person> updatePersonExchange(@RequestBody Person person, @RequestParam("personId") ObjectId personId){
        log.info("RestTemplatePersonController updatePerson() called for PersonId: {} and Person :: {}",personId,person);
        return restTemplatePersonClient.updatePersonExchange(person, personId);
    }

    @DeleteMapping
    public ResponseEntity<?> deletePerson(@RequestParam(value="id") ObjectId id){
        log.info("RestTemplatePersonController deletePerson() called");
        restTemplatePersonClient.deletePerson(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-exchange")
    public ResponseEntity<Void> deletePersonExchange(@RequestParam(value="id") ObjectId id){
        log.info("RestTemplatePersonController deletePersonExchange() called");
        return restTemplatePersonClient.deletePersonExchange(id);
    }

    @GetMapping("/search-person-exchange")
    public ResponseEntity<?> searchPersonExchange(
                  @RequestParam(required = false) String name,
                  @RequestParam(value = "min",required = false) Integer minAge,
                  @RequestParam(value = "max",required = false) Integer maxAge,
                  @RequestParam(required = false) String city,
                  @RequestParam(defaultValue = "0") Integer page,
                  @RequestParam(defaultValue = "5") Integer size
    ){
        log.info("RestTemplatePersonController searchPersonExchange() called");
        /*Optional<String> optName;
        if(!name.isBlank()) {
            optName = Optional.of(name);
        }
        if (minAge!=null && maxAge!=null) {
            Optional<Integer> optMin = Optional.of(minAge);
            Optional<Integer> optMax = Optional.of(maxAge);
        }
        if(!city.isBlank()) {
            Optional<String> optCity = Optional.of(city);
        }
        if (page!=null && size!=null) {
            Optional<Integer> optPage = Optional.of(page);
            Optional<Integer> optSize = Optional.of(size);
        }*/
        PagedModel<PersonResource> personResourcePagedModel = restTemplatePersonClient.searchPersonExchange(Optional.ofNullable(name), Optional.ofNullable(minAge), Optional.ofNullable(maxAge), Optional.ofNullable(city),
                Optional.ofNullable(page), Optional.ofNullable(size));
        return ResponseEntity.status(HttpStatus.OK).body(personResourcePagedModel);
    }

}
