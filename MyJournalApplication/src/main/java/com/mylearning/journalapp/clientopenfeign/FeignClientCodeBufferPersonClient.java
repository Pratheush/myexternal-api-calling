package com.mylearning.journalapp.clientopenfeign;

import com.mylearning.journalapp.clientexception.PersonCallingClientException;
import com.mylearning.journalapp.clientexception.PersonCallingServerException;
import com.mylearning.journalapp.clientexception.PersonNotFoundException;
import com.mylearning.journalapp.clientresponse.Person;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FeignClientCodeBufferPersonClient {

    private final PersonFeignClient personFeignClient;

    public FeignClientCodeBufferPersonClient(PersonFeignClient personFeignClient) {
        this.personFeignClient = personFeignClient;
    }

    public List<Document> getPopulationByCity(){

        ResponseEntity<List<Document>> populationByCityResponse = personFeignClient.getPopulationByCity();
        if (populationByCityResponse.getStatusCode().is2xxSuccessful()) {
            //Process response body
            List<Document> personList = populationByCityResponse.getBody();
            if(personList.isEmpty()) throw new PersonNotFoundException("Population By City Not Found");
            return personList;
        } else if(populationByCityResponse.getStatusCode().is4xxClientError()){
            throw new PersonCallingClientException("Bad Request");
        } else {
            throw new PersonCallingServerException("Server Error");
        }
    }

    public List<Document> getOldestPersonByCity(){
        ResponseEntity<List<Document>> populationByCityResponse = personFeignClient.getOldestPersonByCity();
        if (populationByCityResponse.getStatusCode().is2xxSuccessful()) {
            //Process response body
            List<Document> personList = populationByCityResponse.getBody();
            if(personList.isEmpty()) throw new PersonNotFoundException("Population By City Not Found");
            return personList;
        } else if(populationByCityResponse.getStatusCode().is4xxClientError()){
            throw new PersonCallingClientException("Bad Request");
        } else {
            throw new PersonCallingServerException("Server Error");
        }
    }

    public Person getPersonByNameAndAgePathVariableExchange(String name, Integer age){
        ResponseEntity<Person> personByNameAndAge = personFeignClient.getPersonByNameAndAgePathVariableExchange(name, age);
        if (personByNameAndAge.getStatusCode().is2xxSuccessful()) {
            //Process response body
            Person person = personByNameAndAge.getBody();
            if(person.getPersonId()==null) throw new PersonNotFoundException("Person Not Found");
            return person;
        } else if(personByNameAndAge.getStatusCode().is4xxClientError()){
            throw new PersonCallingClientException("Bad Request");
        } else {
            throw new PersonCallingServerException("Server Error");
        }
    }

    public Person createPersonExchange(Person person){
        ResponseEntity<Person> personCreatedResponse = personFeignClient.createPersonExchange(person);
        if (personCreatedResponse.getStatusCode().is2xxSuccessful()) {
            //Process response body
            Person createdPerson = personCreatedResponse.getBody();
            if(createdPerson.getPersonId()==null) throw new PersonNotFoundException("Person Not Created");
            return createdPerson;
        } else if(personCreatedResponse.getStatusCode().is4xxClientError()){
            throw new PersonCallingClientException("Bad Request");
        } else {
            throw new PersonCallingServerException("Server Error");
        }
    }

    public Person updatePersonByExchange(Person person, ObjectId personId){
        ResponseEntity<Person> personUpdatedResponse = personFeignClient.updatePersonByExchange(person,personId);
        if (personUpdatedResponse.getStatusCode().is2xxSuccessful()) {
            //Process response body
            Person updatedPerson = personUpdatedResponse.getBody();
            if(updatedPerson.getPersonId()==null) throw new PersonNotFoundException("Person Not Updated");
            return updatedPerson;
        } else if(personUpdatedResponse.getStatusCode().is4xxClientError()){
            throw new PersonCallingClientException("Bad Request");
        } else {
            throw new PersonCallingServerException("Server Error");
        }
    }

    public boolean deletePersonByExchange(ObjectId personId){
        ResponseEntity<Void> voidResponse = personFeignClient.deletePersonByExchange(personId);
        if (voidResponse.getStatusCode().is2xxSuccessful()) {
            log.info("Person Deleted");
            return true;
        } else if(voidResponse.getStatusCode().is4xxClientError()){
            throw new PersonCallingClientException("Bad Request");
        } else {
            throw new PersonCallingServerException("Server Error");
        }
    }

    public Object searchPersonByExchange(Optional<String> name, Optional<Integer> minAge,
                                         Optional<Integer> maxAge, Optional<String> city,
                                         Optional<Integer> page, Optional<Integer> size){
        log.info("FeignClientCodeBufferPersonClient searchPersonByExchange called");
        Object personResources = personFeignClient.searchPersonByExchange(
                name.orElse(null),  // Convert Optional to null if empty
                minAge.orElse(null),
                maxAge.orElse(null),
                city.orElse(null),
                page.orElse(0),     // Default to 0 if page is empty
                size.orElse(5)
        );
        log.info("FeignClientCodeBufferPersonClient searchPersonByExchange personResources :: {}", personResources);
        return personResources;
    }
}
