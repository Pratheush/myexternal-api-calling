package com.mylearning.journalapp.clientcontroller;

import com.mylearning.journalapp.client.RestClientCodeBufferPersonClient;
import com.mylearning.journalapp.clientresponse.Person;
import com.mylearning.journalapp.clientresponse.PersonResource;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest-client-person")
@Slf4j
public class RestClientPersonController {

    private final RestClientCodeBufferPersonClient personRestClient;

    public RestClientPersonController(RestClientCodeBufferPersonClient personRestClient) {
        this.personRestClient = personRestClient;
    }

    @GetMapping("/population-by-city")
    public ResponseEntity<List<Document>> getPopulationByCity(){
        List<Document> populationByCity = personRestClient.getPopulationByCity();
        return ResponseEntity.ok(populationByCity);
    }

    @GetMapping("/oldest-person-by-city")
    public ResponseEntity<List<Document>> getOldestPersonByCity(){
        return personRestClient.getOldestPersonByCity();
    }

    @GetMapping("/{name}/{age}")
    public ResponseEntity<Person> getPersonByNameAndAgePathVariable(@PathVariable String name, @PathVariable Integer age){
        Person personByNameAndAgePathVariable = personRestClient.getPersonByNameAndAgePathVariable(name, age);
        return ResponseEntity.ok(personByNameAndAgePathVariable);
    }

    @GetMapping("/age")
    public ResponseEntity<List<Person>> getPersonByAge(@RequestParam Integer min, @RequestParam Integer max){
        List<Person> personByAge = personRestClient.getPersonByAge(min, max);
        return ResponseEntity.ok(personByAge);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchPerson(
            @RequestParam(required = false) String name,
            @RequestParam(value = "min",required = false) Integer minAge,
            @RequestParam(value = "max",required = false) Integer maxAge,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size
    ){
        Object objectPerson = personRestClient.searchPerson(Optional.ofNullable(name), Optional.ofNullable(minAge),
                Optional.ofNullable(maxAge), Optional.ofNullable(city),
                Optional.ofNullable(page), Optional.ofNullable(size));
        log.info("RestClientPersonController searchPerson pagedModelPersonResources :: {}",objectPerson);
        return ResponseEntity.ok(objectPerson);
    }

    // this takes time for execution maybe due to .toBodilessEntity(); used
    @PostMapping("/bodiless-entity")
    public ResponseEntity<Void> savePersonBodilessEntity(@RequestBody Person person){
        return personRestClient.savePersonBodilessEntity(person);
    }

    @PostMapping("/save-person-body")
    public ResponseEntity<ObjectId> savePersonBody(@RequestBody Person person){
        ObjectId personId = personRestClient.savePersonBody(person);
        return ResponseEntity.status(HttpStatus.CREATED).body(personId);
    }

    @PostMapping("/create-person-to-entity")
    public ResponseEntity<Person> createPerson(@RequestBody Person person){
        return personRestClient.createPerson(person);
    }

    @PutMapping("/update-person-to-entity")
    public ResponseEntity<Person> updatePerson(@RequestBody Person person,@RequestParam ObjectId personId){
        return personRestClient.updatePerson(person,personId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable ObjectId id){
        Void voidPerson = personRestClient.deletePerson(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(voidPerson);
    }

    @DeleteMapping("/exchange-delete/{id}")
    public ResponseEntity<Void> deletePersonExchange(@PathVariable ObjectId id){
        log.info("RestClientCodeBufferPersonClient deletePersonExchange called with id: {}",id);
        Void voidPerson = personRestClient.deletePersonExchange(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(voidPerson);
    }

    @PutMapping("/exchange-update/{id}")
    public ResponseEntity<Person> updatePersonExchange(@RequestBody Person person,@PathVariable ObjectId id){
        Person updatedPerson = personRestClient.updatePersonExchange(person, id);
        return ResponseEntity.status(HttpStatus.OK).body(updatedPerson);
    }

    @GetMapping("/exchange-search")
    public ResponseEntity<Object> searchPersonExchange(@RequestParam(required = false) String name,
                                                       @RequestParam(value = "min",required = false) Integer minAge,
                                                       @RequestParam(value = "max",required = false) Integer maxAge,
                                                       @RequestParam(required = false) String city,
                                                       @RequestParam(defaultValue = "0") Integer page,
                                                       @RequestParam(defaultValue = "5") Integer size){
        Object pagedModelPersonResource = personRestClient.searchPersonExchange(Optional.ofNullable(name), Optional.ofNullable(minAge),
                Optional.ofNullable(maxAge), Optional.ofNullable(city),
                Optional.ofNullable(page), Optional.ofNullable(size));
        return ResponseEntity.status(HttpStatus.OK).body(pagedModelPersonResource);
    }


    //================================================================================================

    @GetMapping("/population-by-city-exchange")
    public ResponseEntity<List<Document>> getPopulationByCityExchange(){
        List<Document> populationByCity = personRestClient.getPopulationByCityExchange();
        return ResponseEntity.ok(populationByCity);
    }

    @GetMapping("/oldest-person-by-city-exchange")
    public ResponseEntity<List<Document>> getOldestPersonByCityExchange(){
        List<Document> populationByCity = personRestClient.getOldestPersonByCityExchange();
        return ResponseEntity.ok(populationByCity);
    }

    @GetMapping("/person-by-age-name-exchange/{name}/{age}")
    public ResponseEntity<Person> getPersonByNameAndAgePathVariableExchange(@PathVariable String name, @PathVariable Integer age){
        Person personFrmDB = personRestClient.getPersonByNameAndAgePathVariableExchange(name,age);
        return ResponseEntity.ok(personFrmDB);
    }

    @GetMapping("/all-persons-by-exchange")
    public ResponseEntity<List<PersonResource>> getAllPersonByExchange(){
        List<PersonResource> personsFrmDB = personRestClient.getAllPersonByExchange();
        return ResponseEntity.ok(personsFrmDB);
    }

    @PostMapping("/create-person-exchange")
    public ResponseEntity<Person> createPersonExchange(@RequestBody Person person){
        Person savedPerson = personRestClient.createPersonExchange(person);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPerson);
    }

    @PutMapping("/update-person-exchange")
    public ResponseEntity<Person> updatePersonByExchange(@RequestBody Person person,@RequestParam ObjectId id){
        Person updatedPersonFrmDB = personRestClient.updatePersonByExchange(person,id);
        return ResponseEntity.status(HttpStatus.OK).body(updatedPersonFrmDB);
    }

    @DeleteMapping("/delete-person-exchange/{id}")
    public ResponseEntity<Void> deletePersonByExchange(@PathVariable ObjectId id){
        personRestClient.deletePersonByExchange(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search-person-by-exchange")
    public ResponseEntity<Object> searchPersonByExchange(
            @RequestParam(required = false) String name,
            @RequestParam(value = "min",required = false) Integer minAge,
            @RequestParam(value = "max",required = false) Integer maxAge,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size
    ){
        Object personResources = personRestClient.searchPersonByExchange(
                Optional.ofNullable(name), Optional.ofNullable(minAge),
                Optional.ofNullable(maxAge), Optional.ofNullable(city),
                Optional.ofNullable(page), Optional.ofNullable(size)
        );
        log.info("RestClientPersonController searchPersonByExchange pagedModelPersonResources :: {}",personResources);
        return ResponseEntity.ok(personResources);
    }
}
