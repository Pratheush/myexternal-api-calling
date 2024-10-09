package com.mylearning.journalapp.clientopenfeign;

import com.mylearning.journalapp.clientconfig.MyPersonClientInterface;
import com.mylearning.journalapp.clientexception.PersonCallingClientException;
import com.mylearning.journalapp.clientexception.PersonCallingServerException;
import com.mylearning.journalapp.clientexception.PersonNotFoundException;
import com.mylearning.journalapp.clientresponse.JWTAuthResponse;
import com.mylearning.journalapp.clientresponse.LoginDto;
import com.mylearning.journalapp.clientresponse.Person;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(name = "person.client.type", havingValue = "open-feign")
public class FeignClientCodeBufferPersonClient implements MyPersonClientInterface {

    private static String JWT_TOKEN = null;

    private final PersonFeignClient personFeignClient;
    private final PersonLoginFeignClient personLoginFeignClient;

    private final PersonFeignClientUsingRequestHeader personFeignClientUsingRequestHeader;

    @Autowired
    public FeignClientCodeBufferPersonClient(PersonFeignClient personFeignClient, PersonLoginFeignClient personLoginFeignClient, PersonFeignClientUsingRequestHeader personFeignClientUsingRequestHeader) {
        this.personFeignClient = personFeignClient;
        this.personLoginFeignClient = personLoginFeignClient;
        this.personFeignClientUsingRequestHeader = personFeignClientUsingRequestHeader;
    }

    public List<Document> getPopulationByCity(){
        log.info("FeignClientCodeBufferPersonClient getPopulationByCity() called");
        String jwtAccessToken = getJwtAccessToken();
        log.info("FeignClientCodeBufferPersonClient getPopulationByCity() jwtAccessToken {}", jwtAccessToken);
        ResponseEntity<List<Document>> populationByCityResponse =
                personFeignClientUsingRequestHeader.getPopulationByCity(jwtAccessToken);
        log.info("FeignClientCodeBufferPersonClient getPopulationByCity() populationByCityResponse {}", populationByCityResponse.getBody());
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

    @Override
    public JWTAuthResponse login(LoginDto loginDto) {
        log.info("FeignClientCodeBufferPersonClient login called");

        JWTAuthResponse jwtAuthResponse = personLoginFeignClient.login(loginDto);

        log.info("FeignClientCodeBufferPersonClient jwtAuthResponse : {}",jwtAuthResponse);
        if( jwtAuthResponse!=null && !jwtAuthResponse.getAccessToken().isBlank() && !jwtAuthResponse.getAccessToken().isEmpty()){
            JWT_TOKEN = jwtAuthResponse.getAccessToken();
        }

        return jwtAuthResponse;
    }

    @Override
    public String getJwtAccessToken(){
        log.info("FeignClientCodeBufferPersonClient getJwtAccessToken called");
        return "Bearer " + JWT_TOKEN;
    }
}
