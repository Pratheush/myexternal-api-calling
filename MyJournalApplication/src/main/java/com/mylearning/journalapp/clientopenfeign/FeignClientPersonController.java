package com.mylearning.journalapp.clientopenfeign;

import com.mylearning.journalapp.clientresponse.Person;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/feign-client-person")
@Slf4j
public class FeignClientPersonController {

    private final FeignClientCodeBufferPersonClient feignPersonService;

    public FeignClientPersonController(FeignClientCodeBufferPersonClient feignPersonService) {
        this.feignPersonService = feignPersonService;
    }

    @GetMapping("/population-by-city")
    public ResponseEntity<List<Document>> getPopulationByCity(){
        List<Document> populationByCity = feignPersonService.getPopulationByCity();
        return ResponseEntity.ok(populationByCity);
    }

    @GetMapping("/oldest-person-by-city")
    public ResponseEntity<List<Document>> getOldestPersonByCity(){
        List<Document> oldestPersonByCity = feignPersonService.getOldestPersonByCity();
        return ResponseEntity.ok(oldestPersonByCity);
    }

    @GetMapping("/{name}/{age}")
    public ResponseEntity<Person> getPersonByNameAndAgePathVariableExchange(@PathVariable String name, @PathVariable Integer age){
        Person personByNameAge = feignPersonService.getPersonByNameAndAgePathVariableExchange(name, age);
        return ResponseEntity.ok(personByNameAge);
    }

    @PostMapping("/create-person")
    public ResponseEntity<Person> createPersonExchange(@RequestBody Person person){
        Person personCreated = feignPersonService.createPersonExchange(person);
        return ResponseEntity.status(HttpStatus.CREATED).body(personCreated);
    }

    @PutMapping("/update-person")
    public ResponseEntity<Person> updatePersonByExchange(@RequestBody Person person, @RequestParam ObjectId personId){
        Person personUpdated = feignPersonService.updatePersonByExchange(person,personId);
        return ResponseEntity.ok(personUpdated);
    }

    @DeleteMapping("/delete-person/{personId}")
    public ResponseEntity<Void> deletePersonByExchange(@PathVariable ObjectId personId){
        boolean booleanResponse = feignPersonService.deletePersonByExchange(personId);
        if(booleanResponse) return ResponseEntity.noContent().build();
        else return ResponseEntity.badRequest().build();
    }

    @GetMapping("/search-person-by-feign")
    public ResponseEntity<Object> searchPersonByExchange(
            @RequestParam(required = false) String name,
            @RequestParam(value = "min",required = false) Integer minAge,
            @RequestParam(value = "max",required = false) Integer maxAge,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size
    ){
        Object personResources = feignPersonService.searchPersonByExchange(
                Optional.ofNullable(name), Optional.ofNullable(minAge),
                Optional.ofNullable(maxAge), Optional.ofNullable(city),
                Optional.ofNullable(page), Optional.ofNullable(size)
        );
        log.info("FeignClientPersonController searchPersonByExchange personResource :: {}",personResources);
        return ResponseEntity.ok(personResources);
    }
}
