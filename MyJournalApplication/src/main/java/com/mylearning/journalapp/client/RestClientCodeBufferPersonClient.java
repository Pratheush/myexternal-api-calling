package com.mylearning.journalapp.client;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mylearning.journalapp.clientconfig.MyPersonClientInterface;
import com.mylearning.journalapp.clientconfig.PersonClient;
import com.mylearning.journalapp.clientexception.PersonCallingClientException;
import com.mylearning.journalapp.clientexception.PersonCallingServerException;
import com.mylearning.journalapp.clientexception.PersonNotFoundException;
import com.mylearning.journalapp.clientresponse.JWTAuthResponse;
import com.mylearning.journalapp.clientresponse.LoginDto;
import com.mylearning.journalapp.clientresponse.Person;
import com.mylearning.journalapp.clientresponse.PersonResource;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * static methods can’t access instance variables and instance methods directly. They need some object reference to do so.
 *
 *  We can use a static variable in an instance method in Java. Static variables belong to the class rather
 *  than any specific instance, so they can be accessed directly within instance methods.
 *
 */
@Component
@Slf4j
@ConditionalOnProperty(name = "person.client.type", havingValue = "rest-client")
public class RestClientCodeBufferPersonClient implements MyPersonClientInterface {

    private static String JWT_TOKEN = null;

    private final RestClient restClient;

    private final PersonClient personClient;
    @Autowired
    public RestClientCodeBufferPersonClient(RestClient restClient, PersonClient personClient) {
        this.restClient = restClient;
        this.personClient = personClient;
    }


    public List<Document> getPopulationByCity(){
        return restClient.get()
                .uri("/populationByCity")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization",getJwtAccessToken())
                .retrieve()
                .body(new ParameterizedTypeReference<List<Document>>() {
                });

    }

    public ResponseEntity<List<Document>> getOldestPersonByCity(){
        return restClient.get()
                .uri("/oldestPerson")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization",getJwtAccessToken())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<Document>>() {});
    }

    public Person getPersonByNameAndAgePathVariable(String fistName, Integer age){
        String personUrl = "http://localhost:8081/api/person/{name}/{age}";

        Map<String,String> mapVal = new HashMap<>();
        mapVal.put("name",fistName);
        mapVal.put("age",String.valueOf(age));

        return restClient.get()
                .uri("/{name}/{age}", mapVal)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization",getJwtAccessToken())
                .retrieve()
                .body(Person.class);
    }


    public List<Person> getPersonByAge(Integer minAge,Integer maxAge) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/age") // try to use .query() and .path() ::>> here .query() didn't work' but .path() did work
                        .queryParam("min",minAge)
                        .queryParam("max",maxAge)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization",getJwtAccessToken())
                .retrieve()
                .body(new ParameterizedTypeReference<List<Person>>() {});
    }


    public Object searchPerson(Optional<String> name, Optional<Integer> minAge,
                                                   Optional<Integer> maxAge, Optional<String> city,
                                                   Optional<Integer> page, Optional<Integer> size) {
        log.info("RestClientCodeBufferPersonClient SearchPerson called");

        AtomicReference<Object> pagedModelObject = new AtomicReference<>();

        //PagedModel body
        Object obj
                = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search") // try to use .query() and .path()
                        .queryParamIfPresent("name", name)
                        .queryParamIfPresent("min", minAge)
                        .queryParamIfPresent("max", maxAge)
                        .queryParamIfPresent("city", city)
                        .queryParam("page", page.orElse(0))
                        .queryParam("size", size.orElse(5))
                        .build())
                .header(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization",getJwtAccessToken())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    log.error("RestClientCodeBufferPersonClient searchPerson Client Side ERROR Occured Status : {}",response.getStatusCode());
                    // Throw an exception with a meaningful message
                    throw new PersonCallingServerException("Client Side Error Occurred: ");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request,response) -> {
                    log.error("RestClientCodeBufferPersonClient searchPerson Server Side ERROR Occured Status : {}",response.getStatusCode());
                    // Throw an exception with a meaningful message
                    throw new PersonCallingServerException("Server Side Error Occurred: ");
                })
                .body(Object.class);    // only this one worked in my case
                //.toEntity(PagedModel.class).getBody();
                //.toEntity(new ParameterizedTypeReference<PagedModel<PersonResource>>() {}).getBody();
                //.body(new ParameterizedTypeReference<PagedModel<PersonResource>>() {});
                //.body(PagedModel.class);// Map response to PagedModel<PersonResource>

        log.info("RestClientCodeBufferPersonClient searchPerson personResources :: Object outside {}", obj);
        log.info("RestClientCodeBufferPersonClient searchPerson personResources AtomicObject:: {}", pagedModelObject.get());

        //return b;
        return obj;
    }

    public ResponseEntity<Void> savePersonBodilessEntity(Person person){
        String personUrl = "http://localhost:8081/api/person";
         return restClient.post()
                //.uri(personUrl)
                .contentType(MediaType.APPLICATION_JSON)
                 .header("Authorization",getJwtAccessToken())
                .body(person)
                .retrieve()
                .toBodilessEntity();
    }

    public ObjectId savePersonBody(Person person){
        String personUrl = "http://localhost:8081/api/person";
        return restClient.post()
                //.uri(personUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization",getJwtAccessToken())
                .body(person)
                .retrieve()
                .body(ObjectId.class);
    }


    public ResponseEntity<Person> createPerson(Person person){
        String personUrl = "http://localhost:8081/api/person";
        return restClient.post()
                .uri("/create-person-on-status")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization",getJwtAccessToken())
                .body(person)
                .retrieve()
                .toEntity(Person.class);
    }

    public ResponseEntity<Person> updatePerson(Person updatePerson, ObjectId personId){
        return restClient.put()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("personId",personId)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization",getJwtAccessToken())
                .body(updatePerson)
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    throw new PersonNotFoundException("PERSON NOT FOUND TO BE DELETED");
                })
                .toEntity(Person.class);

    }

    public Void deletePerson(ObjectId personId) {
        ResponseEntity<Void> bodilessEntity = restClient.delete()
                .uri("/{id}", personId)
                .header("Authorization",getJwtAccessToken())
                .retrieve()
                .toBodilessEntity();
        return bodilessEntity.getBody();
    }

    public Void deletePersonExchange(ObjectId personId) {
        return restClient.delete()
                .uri("/{id}", personId)
                .header("Authorization",getJwtAccessToken())
                .exchange((request, response) ->{

                    if(response.getStatusCode().is4xxClientError()) {
                        log.error("RestClientCodeBufferPersonClient deletePersonExchange Client Side Error Occured");
                        throw new PersonCallingClientException("CLIENT SIDE ERROR OCCURRED"+response.getStatusCode());
                    }
                    if (response.getStatusCode().is5xxServerError()) {
                        log.error("RestClientCodeBufferPersonClient deletePersonExchange Server Side Error Occured");
                        throw new PersonCallingServerException("SERVER SIDE ERROR OCCURRED"+response.getStatusCode());
                    }
                    return response.bodyTo(Void.class);

                });
    }

    public Person updatePersonExchange(Person updatePerson, ObjectId personId){
        return restClient.put()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("personId", personId)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization",getJwtAccessToken())
                .body(updatePerson)
                .exchange((request, response) -> {
                    if (response.getStatusCode().is4xxClientError()) {
                        log.error("RestClientCodeBufferPersonClient deletePersonExchange Client Side Error Occured");
                        throw new PersonCallingClientException("CLIENT SIDE ERROR OCCURRED" + response.getStatusCode());
                    }
                    if (response.getStatusCode().is5xxServerError()) {
                        log.error("RestClientCodeBufferPersonClient deletePersonExchange Server Side Error Occured");
                        throw new PersonCallingServerException("SERVER SIDE ERROR OCCURRED" + response.getStatusCode());
                    }
                    return Objects.requireNonNull(response.bodyTo(Person.class));
                });
    }

    public Object searchPersonExchange(Optional<String> name, Optional<Integer> minAge,
                                                           Optional<Integer> maxAge, Optional<String> city,
                                                           Optional<Integer> page, Optional<Integer> size) {
        log.info("RestClientCodeBufferPersonClient searchPersonExchange called");

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search") // try to use .query() and .path()
                        .queryParamIfPresent("name", name)
                        .queryParamIfPresent("min", minAge)
                        .queryParamIfPresent("max", maxAge)
                        .queryParamIfPresent("city", city)
                        .queryParam("page", page.orElse(0))
                        .queryParam("size", size.orElse(5))
                        .build())
                .header("Authorization",getJwtAccessToken())
                .exchange((request, response) -> {
                    if (response.getStatusCode().is4xxClientError()) {
                        log.error("Client-side error occurred");
                        throw new PersonCallingClientException("CLIENT SIDE ERROR OCCURRED: " + response.getStatusCode());
                    } else if (response.getStatusCode().is5xxServerError()) {
                        log.error("Server-side error occurred");
                        throw new PersonCallingServerException("SERVER SIDE ERROR OCCURRED: " + response.getStatusCode());
                    }
                    else if (response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200))) {

                        // below commented code to figure out an error or exception while processing the response from Remote API
                        // Manually read the response body from InputStream
                        /*try (InputStream bodyStream = response.getBody()) {
                            if (bodyStream == null) {
                                throw new RuntimeException("Received null response body");
                            }

                            String responseBody = new BufferedReader(new InputStreamReader(bodyStream))
                                    .lines().collect(Collectors.joining("\n"));

                            log.info("Raw Response Body: {}", responseBody);

                            // Create a custom ObjectMapper to handle _embedded
                            ObjectMapper objectMapper = new ObjectMapper();
                            objectMapper.registerModule(new Jackson2HalModule()); // Register Spring HATEOAS module

                            // Deserialize the response using PagedModel with PersonResource
                            JavaType pagedModelType = objectMapper.getTypeFactory()
                                    .constructParametricType(PagedModel.class, PersonResource.class);
                            return objectMapper.readValue(responseBody, pagedModelType);

                        } catch (IOException e) {
                            log.error("Error reading the response body", e);
                            throw new RuntimeException("Error deserializing response", e);
                        }*/
                        return Objects.requireNonNull(response.bodyTo(Object.class));
                        //return response.bodyTo(new ParameterizedTypeReference<PagedModel<PersonResource>>() { }); // this did not work to get response
                    } else {
                        throw new PersonCallingClientException("INVALID ARGUMENT EXCEPTION");
                    }
                });
    }


    // =================================================================================================

    /**
     * PersonClient RESTCLIENT USAGE :::::::::::::::::::::::::::::::::
     * @return
     */

    public List<Document> getOldestPersonByCityExchange(){
        List<Document> oldestPersonByCity = personClient.getOldestPersonByCityExchange();
        return oldestPersonByCity;
    }

    public List<Document> getPopulationByCityExchange(){
        return personClient.getPopulationByCityExchange();
    }

    public Person getPersonByNameAndAgePathVariableExchange(String fistName, Integer age){
        return personClient.getPersonByNameAndAgePathVariableExchange(fistName, age);
    }

    public Person createPersonExchange(Person person){
        return personClient.createPersonExchange(person);
    }

    public Person updatePersonByExchange(Person updatePerson, ObjectId personId){
        return personClient.updatePersonByExchange(updatePerson, personId);
    }

    public void deletePersonByExchange(ObjectId personId) {
        personClient.deletePersonByExchange(personId);
    }

    public List<PersonResource> getAllPersonByExchange(){
        return personClient.getAllPersonsByExchange();
    }
    // putExchange, deleteExchange, searchExchange
    public Object searchPersonByExchange(Optional<String> name, Optional<Integer> minAge,
                                                   Optional<Integer> maxAge, Optional<String> city,
                                                   Optional<Integer> page, Optional<Integer> size) {
        log.info("RestClientCodeBufferPersonClient searchPersonByExchange called");

        Object personResources = personClient.searchPersonByExchange(
                name.orElse(null),  // Convert Optional to null if empty
                minAge.orElse(null),
                maxAge.orElse(null),
                city.orElse(null),
                page.orElse(0),     // Default to 0 if page is empty
                size.orElse(5)
        );
        log.info("RestClientCodeBufferPersonClient searchPersonByExchange personResources :: {}", personResources);
        return personResources;
    }

    public JWTAuthResponse login(LoginDto loginDto) {
        log.info("RestClientCodeBufferPersonClient login called");
        String personUrl = "http://localhost:8081/api/codebuffer/person/login";
        JWTAuthResponse jwtAuthResponse = restClient.post()
                .uri(personUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginDto)
                .retrieve()
                .body(JWTAuthResponse.class);
        log.info("RestClientCodeBufferPersonClient jwtAuthResponse : {}",jwtAuthResponse);
        if( jwtAuthResponse!=null && !jwtAuthResponse.getAccessToken().isBlank() && !jwtAuthResponse.getAccessToken().isEmpty()){
            JWT_TOKEN = jwtAuthResponse.getAccessToken();
        }
        return jwtAuthResponse;

    }

    public String getJwtAccessToken(){
        return "Bearer " + JWT_TOKEN;
    }

}
