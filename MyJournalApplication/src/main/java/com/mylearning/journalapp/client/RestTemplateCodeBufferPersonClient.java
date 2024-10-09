package com.mylearning.journalapp.client;

import com.mylearning.journalapp.clientconfig.MyPersonClientInterface;
import com.mylearning.journalapp.clientexception.PersonCallingClientException;
import com.mylearning.journalapp.clientexception.PersonCallingServerException;
import com.mylearning.journalapp.clientexception.PersonNotFoundException;
import com.mylearning.journalapp.clientresponse.*;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;

import org.springframework.hateoas.PagedModel;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *  RestTemplate Methods to Make GET Requests
 *  In RestTemplate, the available methods for executing GET APIs are:
 * 1. getForObject(url, classType) – retrieve a representation by doing a GET on the URL. The response (if any) is unmarshalled to the given class type and returned.
 * 2. getForEntity(url, responseType) – retrieve a representation as ResponseEntity by doing a GET on the URL.
 * 3. exchange(url, httpMethod, requestEntity, responseType) – execute the specified RequestEntity and return the response as ResponseEntity.
 * 4. execute(url, httpMethod, requestCallback, responseExtractor) – execute the httpMethod to the given URI template, prepare the request with the RequestCallback, and read the response with a ResponseExtractor.
 *
 * Receiving API Response as XML or JSON String
 * This is pretty useful when we are getting an unparsable response from the server, and we have no control over getting it fixed on the server side.
 * We can use the getForEntity() API which returns the ResponseEntity instance. To extract the response body, use its responseEntity.getBody() method.
 *
 * ResponseEntity<String> responseEntity = restTemplate.getForEntity("/users/{id}", String.class, Map.of("id", "1"));
 *
 * ObjectMapper mapper = new ObjectMapper();
 * JsonNode root = mapper.readTree(responseEntity.getBody());
 *
 *----------------------------------------------------------------
 *
 * Sending Request Headers
 * If we want to send the request headers then we need to use the generic exchange() API.
 *
 * HttpHeaders headers = new HttpHeaders();
 * headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
 * headers.set("X-COM-PERSIST", "NO");
 * headers.set("X-COM-LOCATION", "USA");
 *
 * HttpEntity<String> entity = new HttpEntity<String>(headers);
 *
 * ResponseEntity<User[]> responseEntity = restTemplate.exchange("/users", HttpMethod.GET, entity, User[].class);
 *
 * =====================================================================================================================
 *
 * HTTP POST Requests using RestTemplate
 * postForObject(url, request, classType) – POSTs the given object to the URL and returns the representation found in the response as given class type.
 * postForEntity(url, request, responseType) – POSTs the given object to the URL and returns the response as ResponseEntity.
 * postForLocation(url, request, responseType) – POSTs the given object to the URL and returns the value of the Location header.
 * exchange(url, requestEntity, responseType)
 * execute(url, httpMethod, requestCallback, responseExtractor)
 *
 *
 */

@Component
@Slf4j
@ConditionalOnProperty(name = "person.client.type", havingValue = "rest-template")
public class RestTemplateCodeBufferPersonClient implements MyPersonClientInterface {

    private final RestTemplate restTemplate;

    private static String JWT_TOKEN = "";

    public RestTemplateCodeBufferPersonClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Document> getPopulationByCity(){
        // api key any
        //String api_key = ""
        String personUrl = "http://localhost:8081/api/person/populationByCity";
        ResponseEntity<List<Document>> personResponse = restTemplate.exchange(personUrl, HttpMethod.GET, null, ParameterizedTypeReference.forType(List.class));
        return personResponse.getBody();
    }

    public List<Document> getOldestPersonByCity(){
        String personUrl = "http://localhost:8081/api/person/oldestPerson";
        ResponseEntity<List<Document>> personResponse = restTemplate.exchange(personUrl, HttpMethod.GET, null, ParameterizedTypeReference.forType(List.class));
        return personResponse.getBody();
    }

    /**
     * here in map name as key and age as key should match exactly as in the personUrl where in path-variable is specified as {name} and {age}
     */
    public Person getPersonByNameAndAgePathVariableGetForEntity(String fistName, Integer age){
        String personUrl = "http://localhost:8081/api/person/{name}/{age}";
        Map<String,String> mapVal = new HashMap<>();
        mapVal.put("name",fistName);
        mapVal.put("age",String.valueOf(age));
        ResponseEntity<Person> personResponse = restTemplate.getForEntity(personUrl, Person.class,mapVal );
        return personResponse.getBody();
    }

    public Person getPersonByNameAndAgePathVariableGetForObject(String fistName, Integer age){
        String personUrl = "http://localhost:8081/api/person/{name}/{age}";
        Map<String,String> mapVal = new HashMap<>();
        mapVal.put("name",fistName);
        mapVal.put("age",String.valueOf(age));
        //return restTemplate.getForObject(personUrl, Person.class, mapVal ); // this will work
        return restTemplate.getForObject(personUrl, Person.class, fistName,age ); // this will also work
    }

    public Person getPersonByNameAndAgePathVariableGetForExchange(String fistName, Integer age){
        String personUrl = "http://localhost:8081/api/person/{name}/{age}";
        Map<String,String> mapVal = new HashMap<>();
        mapVal.put("name",fistName);
        mapVal.put("age",String.valueOf(age));

        //ResponseEntity<Person> personResponseEntity = restTemplate.exchange(personUrl, HttpMethod.GET, null, Person.class, mapVal);// this will also work
        ResponseEntity<Person> personResponseEntity = restTemplate.exchange(personUrl, HttpMethod.GET, null, Person.class, fistName,age);// this will also work
        return personResponseEntity.getBody();
    }

    /**
     * // Method 4: Using execute()
     *   execute(url, httpMethod, requestCallback, responseExtractor)
     *
     *   RequestCallback requestCallback = request -> {
     *   // You can customize the request if needed
     *   };
     *
     *   ResponseExtractor<ResponseEntity<User>> responseExtractor
     *   = restTemplate.responseEntityExtractor(User.class);
     *
     * @param fistName
     * @param age
     * @return Person
     */
    public Person getPersonByNameAndAgePathVariableGetForExecute(String fistName, Integer age){
        String personUrl = "http://localhost:8081/api/person/{name}/{age}";

        Map<String,String> mapVal = new HashMap<>();
        mapVal.put("name",fistName);
        mapVal.put("age",String.valueOf(age));

        RequestCallback requestCallback = request -> {
            // You can customize the request if needed

        };

        ResponseExtractor<ResponseEntity<Person>> responseExtractor
                = restTemplate.responseEntityExtractor(Person.class);

        ResponseEntity<Person> personResponseEntity = restTemplate.execute(personUrl,HttpMethod.GET,requestCallback,responseExtractor,mapVal);// this will also work
        //ResponseEntity<Person> personResponseEntity = restTemplate.execute(personUrl,HttpMethod.GET,requestCallback,responseExtractor,fistName,age);// this will also work

        if(personResponseEntity!=null) return personResponseEntity.getBody();
        else throw new PersonNotFoundException("Person Does Not Exist With Name :"+fistName+" Age :"+age);

    }

    public ObjectId savePersonPostForObject(Person person){
        String personUrl = "http://localhost:8081/api/person";
        return restTemplate.postForObject(personUrl, person, ObjectId.class);
    }

    public URI savePersonPostForLocation(Person person){
        String personUrl = "http://localhost:8081/api/person";
        return restTemplate.postForLocation(personUrl, person, ObjectId.class);
    }

    public ObjectId savePersonPostForEntity(Person person){
        String personUrl = "http://localhost:8081/api/person";
        ResponseEntity<ObjectId> objectIdResponseEntity = restTemplate.postForEntity(personUrl, person, ObjectId.class);
        HttpHeaders httpHeaders = objectIdResponseEntity.getHeaders();
        log.info("Response Headers : {}",httpHeaders);
        return objectIdResponseEntity.getBody();
    }

    /**
     * ERROR :: The error you're encountering is due to a mismatch in the type conversion for personId. The @RequestParam is accepting String, but it's trying to bind to an ObjectId
     *
     * The personId in the request is passed as a String, and you're trying to bind it directly to ObjectId, which causes a type conversion issue.
     * because Spring is expecting an ObjectId but is receiving a String
     * handle the personId conversion properly before sending it in the request URL
     * The personId is converted to a String using personId.toString() when appending it to the URL.
     * The RestTemplate call also ensures that personId is properly handled as a string when constructing the URL.
     * @param person
     * @param personId
     *
     *  Exception Handling
     */
    public ResponseEntity<Person> updatePersonExchange(Person person,ObjectId personId) {
        log.info("RestTemplateCodeBufferPersonClient UpdatePerson called for Person :: {} and PersonId :: {}",person,personId);

        // Simulating a client side error in request to check Error Handling
        //String personUrl = "http://localhost:8081/api/person?personId=";

        // Convert ObjectId to String for the URL
        //String personUrl = "http://localhost:8081/api/person?personId=" + personId.toString(); //this will also work. here personId is passed as String since @RequestParam is expecting a String not ObjectId
        String personUrl = "http://localhost:8081/api/person?personId=" + "{person-id}";   // this will also work. here personId is passed as String through PathVariable and later passed its values as Uri-Variables as String .

        // Set headers
        // if there is authentication and authorization for the api then we can send Authorization header here which has username and password
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Create HttpEntity with Person object and headers
        // we can also send request body value like this
        HttpEntity<Person> personEntity = new HttpEntity<>(person,headers);

        try {
            // send personId as String as Uri Variables when we are using personUrl with PathVariable at the end concatenated with url
            // Make the PUT request
            //restTemplate.put(personUrl, personEntity, String.valueOf(personId)); // this will also work. here uri variables for the request we called

            ResponseEntity<Person> personResponseEntity = restTemplate.exchange(personUrl, HttpMethod.PUT, personEntity, Person.class,String.valueOf(personId)); // here uri variables for the request we called. this will also work

            /**
             * String personUrl = "http://localhost:8081/api/person?personId=" + personId.toString(); here at the end personId is concatenated as String
             * use below restTemplate.exchange() statement code when you are using above given personUrl where personId.toString() is added in the url before sending request.
             */
            //ResponseEntity<Person> personResponseEntity = restTemplate.exchange(personUrl, HttpMethod.PUT, personEntity, Person.class); // here no uri variables for the request we called. this will also work

            // Return the updated person
            // return personResponseEntity.getBody(); // even though we are using and suppose to get ResponseEntity<Person> but still returning nothing in response so we are commenting this statement out

            // Process the response
            if (personResponseEntity.getStatusCode().is2xxSuccessful()) {
                Person personEntityBody = personResponseEntity.getBody();
                log.info("Response: " + personEntityBody);
                return personResponseEntity;
            } else {
               log.error("Unexpected HTTP status: " + personResponseEntity.getStatusCode());
                throw new PersonCallingClientException("Unexpected HTTP status:"+ personResponseEntity.getStatusCode());
            }
        }catch (HttpClientErrorException e) {
            // Handle HTTP client errors (4xx status codes)
            if (e.getStatusCode().is4xxClientError()) {
                log.error("Client error: " + e.getStatusCode() + " - " + e.getStatusText());
                log.error("Response Body: " + e.getResponseBodyAsString());
                throw new PersonCallingClientException(e.getMessage());
            } else if (e.getStatusCode().is5xxServerError()) {
                log.error("Server error: " + e.getStatusCode() + " - " + e.getStatusText());
                log.error("Response Body: " + e.getResponseBodyAsString());
                throw new PersonCallingServerException(e.getMessage());
            } else {
                log.error("Unexpected HTTP status: " + e.getStatusCode());
                throw new PersonCallingClientException(e.getMessage());
            }
        } catch (Exception e) {
            // Handle other exceptions
            log.error("An error occurred: " + e.getMessage());
            throw new PersonCallingServerException(e.getMessage());
        }
    }

    public void deletePerson(ObjectId id) {
        log.info("RestTemplateCodeBufferPersonClient DeletePerson called");
        String personUrl = "http://localhost:8081/api/person/{id}";
        Map<String,String> mapVal = new HashMap<>();
        mapVal.put("id", String.valueOf(id));
        restTemplate.delete(personUrl, mapVal);
        //restTemplate.delete(personUrl, id);   // this will also work
    }

    public ResponseEntity<Void> deletePersonExchange(ObjectId id) {
        log.info("RestTemplateCodeBufferPersonClient DeletePerson called");
        String personUrl = "http://localhost:8081/api/person/{id}";

        Map<String,String> mapVal = new HashMap<>();
        mapVal.put("id", String.valueOf(id));

        return restTemplate.exchange(personUrl, HttpMethod.DELETE, null, Void.class, mapVal);
    }

    // ResponseEntity<org.springframework.hateoas.PagedModel<PersonResource>>

    public PagedModel<PersonResource> searchPersonExchange(Optional<String> name, Optional<Integer> minAge, Optional<Integer> maxAge,
                                                           Optional<String> city, Optional<Integer> page, Optional<Integer> size){
        log.info("RestTemplateCodeBufferPersonClient SearchPersonExchange called");

        String searchUrl = "http://localhost:8081/api/person/search";

        // UriComponentsBuilder: Constructs the URL with the required query parameters.
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(searchUrl)
                .queryParamIfPresent("name",name)
                .queryParamIfPresent("min",minAge)
                .queryParamIfPresent("max",maxAge)
                .queryParamIfPresent("city",city)
                .queryParamIfPresent("page",page)
                .queryParamIfPresent("size",size);
                /*.queryParam("name", name)
                .queryParam("min", minAge)
                .queryParam("max", maxAge)
                .queryParam("city", city)
                .queryParam("page", page)
                .queryParam("size", size);*/

        // Create headers if needed
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // ResponseEntity<org.springframework.hateoas.PagedModel<PersonResource>>
            // Make a GET request
            // The PersonPageResponse class should map to the structure of the paginated Page<Person> response.
            //ResponseEntity<PersonPageResponse> pageModelResponse =
            ResponseEntity<PagedModel<PersonResource>> pageModelResponse =
                    restTemplate.exchange(
                            urlBuilder.toUriString(),
                            HttpMethod.GET,
                            entity,
                            ParameterizedTypeReference.forType(PagedModel.class)

                    );


            // Process the response
            if (pageModelResponse.getStatusCode().is2xxSuccessful()) {
                pageModelResponse.getBody();
                log.info("Response: " + pageModelResponse.getBody());
                // Return the body of the response
                 return pageModelResponse.getBody();
            } else {
                log.error("Unexpected HTTP status: " + pageModelResponse.getStatusCode());
                throw new PersonCallingClientException(pageModelResponse.getStatusCode().toString());
            }
        }catch (HttpClientErrorException ex) {
            // Handle HTTP client errors (4xx status codes)
            if (ex.getStatusCode().is4xxClientError()) {
                log.error("Client error: " + ex.getStatusCode() + " - " + ex.getStatusText());
                log.error("Response Body: " + ex.getResponseBodyAsString());
                if(ex.getStatusCode().value()== 404) throw new PersonNotFoundException("Person List Not Found");
                throw new PersonCallingClientException(ex.getMessage());
            } else if (ex.getStatusCode().is5xxServerError()) {
                log.error("Server error: " + ex.getStatusCode() + " - " + ex.getStatusText());
                log.error("Response Body: " + ex.getResponseBodyAsString());
                throw new PersonCallingServerException(ex.getMessage());
            } else {
                log.error("Unexpected HTTP status: " + ex.getStatusCode());
                throw new PersonCallingClientException(ex.getMessage());
            }
        } catch (Exception ex) {
            // Handle other exceptions
            log.error("An error occurred: " + ex.getMessage());
            throw new PersonCallingServerException(ex.getMessage());
        }
    }


    @Override
    public JWTAuthResponse login(LoginDto loginDto) {
        log.info("RestTemplateCodeBufferPersonClient login called");

        // Define the URL
        String personUrl = "http://localhost:8081/api/codebuffer/person/login";

        // Set the headers if needed
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Create the HttpEntity object with the headers and the body
        HttpEntity<LoginDto> requestEntity = new HttpEntity<>(loginDto, headers);

        ResponseEntity<JWTAuthResponse> jwtAuthResponseEntity = restTemplate.exchange(personUrl, HttpMethod.POST, requestEntity, JWTAuthResponse.class);
        JWTAuthResponse jwtAuthResponse = jwtAuthResponseEntity.getBody();
        log.info("RestTemplateCodeBufferPersonClient jwtAuthResponse : {}",jwtAuthResponse);
        if( jwtAuthResponse!=null && !jwtAuthResponse.getAccessToken().isBlank() && !jwtAuthResponse.getAccessToken().isEmpty()) JWT_TOKEN = jwtAuthResponse.getAccessToken();
        return jwtAuthResponse;
    }

    @Override
    public String getJwtAccessToken() {
        return "Bearer " + JWT_TOKEN;
    }
}
