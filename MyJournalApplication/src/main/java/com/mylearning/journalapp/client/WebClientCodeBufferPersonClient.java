package com.mylearning.journalapp.client;


import com.mylearning.journalapp.clientexception.PersonCallingClientException;
import com.mylearning.journalapp.clientexception.PersonCallingServerException;
import com.mylearning.journalapp.clientexception.PersonNotFoundException;
import com.mylearning.journalapp.clientresponse.Person;
import com.mylearning.journalapp.clientresponse.PersonResource;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Using WebClient for Sending Requests and Handling Responses
 * Create WebClient.UriSpec reference using prebuilt methods such as get(), put(), post() or delete().
 * Set the request URI if not set already.
 * Set the request headers and authentication details, if any.
 * Set the request body, if any.
 * Call the retrieve() or exchange() method. The retrieve() method directly performs the HTTP request and retrieves the response body. The exchange() method returns ClientResponse having the response status and headers. We can get the response body from ClientResponse instance.
 * Handle the response returned from the server.
 *
 * WebClient retrieve() vs. exchange() APIs::::::::::::::::
 * In Spring WebClient, both the retrieve() and exchange() methods are used for making HTTP requests,
 * but they offer different levels of control and flexibility over the request and response handling.
 *
 * Using retrieve() API:::::::::::::::::
 * The retrieve() is for common use cases where we want to send an HTTP request, receive the response, and handle it in a reactive way.
 * When we call retrieve(), the request is sent, and the response is automatically processed and deserialized into a reactive type
 * (e.g., Mono or Flux). We don’t need to explicitly subscribe to the response.
 * The response type is inferred from the final call to bodyToMono() or bodyToFlux(). For example, if we use bodyToMono(Employee.class), we’ll get a Mono<Employee> as the result.
 *
 * Mono<Employee> employeeMono = webClient.get()
 *     .uri("/employees/{id}", 123)
 *     .retrieve()
 *     .bodyToMono(Employee.class)
 *
 * Please note that bodyToMono() and bodyToFlux() methods always expect a response body of a given class type.
 * If the response status code is 4xx (client error) or 5xx (Server error) i.e. there is no response body then these methods throw WebClientException.
 *
 * Use bodyToMono(Void.class) if no response body is expected. This is helpful in DELETE operations.
 * No response body is expected
 * webClient.delete()
 *   .uri("/employees/" + id)
 *   .retrieve()
 *   .bodyToMono(Void.class);
 *
 * ===============================================================================
 *
 *  Using exchange() API
 *
 *  The exchange() method returns ClientResponse having the response status and headers.
 *  We can get the response body from ClientResponse instance.
 *
 *  The exchange() API allows us to handle the request and response explicitly.
 *  It returns the ClientResponse which has all the response elements such as status, headers and response body as well.
 *
 *  With exchange(), we are responsible for subscribing to the response explicitly using subscribe(), block() or similar methods.
 *  This gives us more control over when and how the request is executed.
 *
 *  When using exchange(), we must always use any of the bodyToMono(), bodyToFlux() or toEntity() methods of ClientResponse
 *  which provides more flexibility in choosing the reactive type for the response.
 *
 *  Mono<ClientResponse> responseMono = webClient.get()
 *     .uri("/employees/{id}", 123)
 *     .exchange();
 *
 * responseMono.subscribe(clientResponse -> {
 *
 *   HttpStatus statusCode = clientResponse.statusCode();  // HTTP Status
 *   HttpHeaders headers = clientResponse.headers();  // HTTP Headers
 *   Mono<Employee> employeeMono = clientResponse.bodyToMono(Employee.class);  // Response Body
 *   // Handle the response, including error handling based on status code
 * });
 *
 *================================================================
 *
 * WHILE TESTING WE CAN USE WebClient ::::
 *
 * WebTestClient
 *   .bindToServer()
 *     .baseUrl("http://localhost:8080")
 *     .build()
 *     .post()
 *     .uri("/resource")
 *   .exchange()
 *     .expectStatus().isCreated()
 *     .expectHeader().valueEquals("Content-Type", "application/json")
 *     .expectBody().jsonPath("field").isEqualTo("value");
 *
 *     ----------------------------------------------------------------
 *
 * Preparing a Request – Define the URL
 *
 * RequestBodySpec bodySpec = uriSpec.uri("/resource");
 *
 * RequestBodySpec bodySpec = uriSpec.uri(
 *   uriBuilder -> uriBuilder.pathSegment("/resource").build());
 *
 * RequestBodySpec bodySpec = uriSpec.uri(URI.create("/resource"));
 *
 * if we defined a default base URL for the WebClient, this last method would override this value.
 *
 */

@Service
@Slf4j
public class WebClientCodeBufferPersonClient {

    private final WebClient webClient;

    public WebClientCodeBufferPersonClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<Document> getPopulationByCity(){
        String personUrl = "http://localhost:8081/api/person/populationByCity";
        try {
        Flux<Document> documentFlux = webClient.get()
                .uri("/populationByCity")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToFlux(Document.class);
        return documentFlux;
        }catch(WebClientException ex){
            throw new PersonCallingClientException("Failed to get population by city due to ex : %s"+ex.getMessage());
        }
    }

    // The exchange() method returns ClientResponse having the response status and headers.
    // We can get the response body from ClientResponse instance.
    public Flux<Document> getOldestPersonByCity(){
        String personUrl = "http://localhost:8081/api/person/oldestPerson";
        try{
        return webClient.get()
                .uri("/oldestPerson")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchangeToFlux(response -> handleResponseForFlux(response, Document.class));
        }catch(WebClientException ex){
            throw new PersonCallingClientException("Failed to get Oldest Person by city due to ex : %s"+ex.getMessage());
        }
    }

    public Mono<Person> getPersonById(ObjectId personId){
       // String personUrl = "http://localhost:8081/api/person/{personId}";
        return webClient.get()
                .uri("/{personId}", personId)
                .retrieve()
                .onStatus(
                        httpStatus -> !httpStatus.is2xxSuccessful(),
                        clientResponse -> handleErrorResponse(clientResponse.statusCode())
                )
                .bodyToMono(Person.class);
    }

    /**
     *
     * The onErrorResume() method in the code is used for error handling in a reactive stream.
     * Specifically, it allows you to define a fallback logic in case an error occurs during
     * the execution of the WebClient request. Instead of propagating the error, onErrorResume()
     * allows you to provide an alternative value (or stream) to resume the flow.
     *
     * onErrorResume() Is Useful:
     * It prevents the propagation of exceptions and allows you to define what should happen when an error occurs.
     * @return
     */
    public Flux<Person> getAllPersons() {
        return webClient.get()
                .uri("/all-person")
                .retrieve()
                .onStatus(httpStatus -> !httpStatus.is2xxSuccessful(),
                        clientResponse -> handleErrorResponse(clientResponse.statusCode()))
                .bodyToFlux(Person.class)
                .onErrorResume(Exception.class, e -> Flux.empty()); // Return an empty collection on error
    }

    public Mono<ObjectId> createPersonWithoutResponseHandling(Person person){
        String personUrl = "http://localhost:8081/api/person";

        Mono<ObjectId> objectIdMono = webClient.post()
                //.uri(personUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(person), Person.class)
                .retrieve()
                .bodyToMono(ObjectId.class);
        return objectIdMono;
    }

    // The exchange() method returns ClientResponse having the response status and headers.
    // We can get the response body from ClientResponse instance.
    public Mono<ObjectId> createPersonWithResponseHandling(Person person){
        String personUrl = "http://localhost:8081/api/person";

        return webClient.post()
                //.uri(personUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(person), Person.class)
                .exchangeToMono(response -> handleResponseForMono(response, ObjectId.class));

    }

    /**
     * toEntity() method in WebClient is used to retrieve the response as a ResponseEntity.
     * This includes not only the response body (the deserialized data) but also the full HTTP response,
     * such as headers and the HTTP status code.
     *
     *
     * When using toEntity(Person.class), the result would allow you to access the following:
     * Body: The Person object.
     * Status: The HTTP status code (e.g., 201 Created, 200 OK, etc.).
     * Headers: HTTP response headers.
     *
     * Mono<ResponseEntity<Person>> responseEntityMono = webClient.post()
     *     .uri("/create-person-on-status")
     *     .body(Mono.just(newPerson), Person.class)
     *     .retrieve()
     *     .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
     *         return Mono.error(new WebClientResponseException(clientResponse.statusCode().value(), "Bad Request", null, null, null));
     *     })
     *     .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
     *         return Mono.error(new WebClientResponseException(clientResponse.statusCode().value(), "Internal Server Error Occurred", null, null, null));
     *     })
     *     .toEntity(Person.class);
     *
     * // Later, you can use responseEntityMono to get the response body, headers, or status code:
     * responseEntityMono.subscribe(responseEntity -> {
     *     Person person = responseEntity.getBody();  // The response body
     *     HttpStatus statusCode = responseEntity.getStatusCode();  // The HTTP status code
     *     HttpHeaders headers = responseEntity.getHeaders();  // Response headers
     * });
     *
     *
     * @param newPerson
     * @return
     */

    public Mono<ResponseEntity<Person>> createPersonOnStatus(Person newPerson) {
        Mono<ResponseEntity<Person>> responseEntityMono = webClient.post()
                .uri("/create-person-on-status")
                .body(Mono.just(newPerson), Person.class)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    //logError("Client error occurred");
                    return Mono.error(new WebClientResponseException
                            (clientResponse.statusCode().value(), "Bad Request", null, null, null));
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    //logError("Server error occurred");
                    return Mono.error(new WebClientResponseException
                            (clientResponse.statusCode().value(), "Internal Server Error Occured", null, null, null));
                })
                .toEntity(Person.class);

        // Later, you can use responseEntityMono to get the response body, headers, or status code:
        responseEntityMono.subscribe(responseEntity -> {
            Person person = responseEntity.getBody();  // The response body
            HttpStatusCode statusCode = responseEntity.getStatusCode();  // The HTTP status code
            HttpHeaders headers = responseEntity.getHeaders();  // Response headers
            log.info("createPersonOnStatus :: person : {}", person);
            log.info("createPersonOnStatus :: statusCode : {}", statusCode);
            log.info("createPersonOnStatus :: headers : {}", headers);
        });

        return responseEntityMono;
    }


    public Mono<Person> updatePerson(Person person,ObjectId personId) {
        return webClient.put()
                .uri("?personId="+"{personId}",personId)
                .body(Mono.just(person), Person.class)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handleClientError)
                .onStatus(HttpStatusCode::is5xxServerError, this::handleServerError)
                .bodyToMono(Person.class);

    }

    public Mono<Void> deletePersonBodyToMono(ObjectId personId) {
        return webClient.delete()
                .uri("/{id}", personId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handleClientError)
                .onStatus(HttpStatusCode::is5xxServerError, this::handleServerError)
                .bodyToMono(Void.class);
    }

    public Mono<Void> deletePersonToEntity(ObjectId personId) {
        Mono<ResponseEntity<Void>> responseEntityMono = webClient.delete()
                .uri("/{id}", personId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handleClientError)
                .onStatus(HttpStatusCode::is5xxServerError, this::handleServerError)
                .toEntity(Void.class);
        return responseEntityMono.then();
    }



    public Flux<PagedModel<PersonResource>> searchPerson(Optional<String> name, Optional<Integer> minAge, Optional<Integer> maxAge,
                                                         Optional<String> city, Optional<Integer> page, Optional<Integer> size) {
        log.info("WebClientCodeBufferPersonClient SearchPerson called");

        String searchUrl = "http://localhost:8081/api/person/search";
        String searchUrlH = "http://localhost:8081/api/person/search-no-hateoas";

        /*
            webClient.get()
            .uri()
            ........
            ........
            inside uri() api
            webClient.get()
                .uri(uriBuilder -> uriBuilder.path(searchUrl)
                        .queryParam("name", name)
                        .queryParam("min", minAge)
                        .queryParam("max", maxAge)
                        .queryParam("city", city)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .........
                .........
         */

        // UriComponentsBuilder: Constructs the URL with the required query parameters.
        String personUriString = UriComponentsBuilder.fromHttpUrl(searchUrl) // we can put full url in searchUrl
                .queryParamIfPresent("name", name)
                .queryParamIfPresent("min", minAge)
                .queryParamIfPresent("max", maxAge)
                .queryParamIfPresent("city", city)
                .queryParam("page", page.orElse(0))
                .queryParam("size", size.orElse(5))
                .toUriString();
                /*.queryParam("name", name)
                .queryParam("min", minAge)
                .queryParam("max", maxAge)
                .queryParam("city", city)
                .queryParam("page", page)
                .queryParam("size", size);*/


        /**
         * The response is expected to be of type PagedModel<PersonResource>, so we use bodyToMono with ParameterizedTypeReference to deserialize it properly.
         * PagedModel<PersonResource> is a representation of a paginated HATEOAS resource, which maps well with the structure returned by the server.
         *
         * .accept(MediaType.APPLICATION_JSON): Sets the Accept header to inform the server that the
         * client expects a JSON response.
         *
         * .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE): Sets the Content-Type header
         * to indicate that the request body, if present, will be in JSON format.
         *
         * .retrieve() is used to trigger the actual request. It prepares the WebClient to retrieve the response and handle it.
         *
         * Handling Status Codes with onStatus():
         * onStatus() is used to handle specific status codes in the HTTP response.
         * HttpStatusCode::is4xxClientError: This checks for 4xx status codes, indicating client-side errors If a client error occurs, it creates and returns a Mono.error() with a custom exception
         * HttpStatusCode::is5xxServerError: This checks for 5xx status codes, server-side errors. If a server error occurs, it returns a Mono.error() with a custom exception
         *
         * onStatus() allows you to handle specific HTTP response status codes (e.g., 4xx and 5xx errors) before
         * processing the body. This enables you to provide meaningful error messages when the server responds
         * with an error status, without needing to check for errors in the response body manually.
         *
         * Handling the Response Body:
         * bodyToFlux() This method deserializes the response body into a reactive Flux stream of PagedModel<PersonResource>.
         * The ParameterizedTypeReference is necessary because PagedModel<PersonResource> is a generic type.
         *
         * doOnError() is used to react to an error that occurs during the request or in the response processing.
         * It doesn't handle the error itself but allows side effects such as logging or additional operations before the error propagates further.
         *
         * is used for side effects (e.g., logging) when an error occurs during any part of the request-response process.
         * This helps in diagnostics without interrupting the actual flow.
         * The doOnError() block doesn’t consume or replace the error; instead, it allows you to perform operations like logging the error,
         * notifying a monitoring system, or handling resource cleanup before the error propagates.
         *
         *
         */
            return webClient.get()
                    .uri(personUriString)
                    //.accept(MediaType.APPLICATION_JSON) if i use this then it will give 400 bad requests error in response after fetching the data from remote api and at remote api data is fetching but it gives error of IllegalArgumentException at PersonController in getPersonById() of Invalid ObjectId
                    //.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header("Content-Type", "application/json")
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                            Mono.error(new PersonCallingClientException("Client error during person search"))
                    )
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                            Mono.error(new PersonCallingServerException("Server error during person search"))
                    )
                    .bodyToFlux(new ParameterizedTypeReference<PagedModel<PersonResource>>() {})
                    //.bodyToFlux(ParameterizedTypeReference.forType(PagedModel.class))
                    .doOnError(error -> {
                        log.error("Error occurred while fetching persons: " + error.getMessage());
                        throw new PersonCallingClientException("ERROR OCCURRED WHILE FETCHING PERSONS : "+ error.getMessage());
                    });
    }

    public Flux<PagedModel<PersonResource>> searchPersonUsingHandleResponseForFlux(Optional<String> name, Optional<Integer> minAge, Optional<Integer> maxAge,
                                                         Optional<String> city, Optional<Integer> page, Optional<Integer> size) {
        log.info("WebClientCodeBufferPersonClient searchPersonUsingHandleResponseForFlux called");

        String searchUrl = "http://localhost:8081/api/person/search";


        /*
            webClient.get()
            .uri()
            ........
            ........
            inside uri() api
            webClient.get()
                .uri(uriBuilder -> uriBuilder.path(searchUrl)
                        .queryParam("name", name)
                        .queryParam("min", minAge)
                        .queryParam("max", maxAge)
                        .queryParam("city", city)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .........
                .........
         */

        // UriComponentsBuilder: Constructs the URL with the required query parameters.
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(searchUrl) // we can put full url in searchUrl
                .queryParamIfPresent("name", name)
                .queryParamIfPresent("min", minAge)
                .queryParamIfPresent("max", maxAge)
                .queryParamIfPresent("city", city)
                .queryParamIfPresent("page", page)
                .queryParamIfPresent("size", size);
                /*.queryParam("name", name)
                .queryParam("min", minAge)
                .queryParam("max", maxAge)
                .queryParam("city", city)
                .queryParam("page", page)
                .queryParam("size", size);*/

        Flux<PagedModel<PersonResource>> pagedModelFlux = webClient.get()
                .uri(urlBuilder.toUriString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchangeToFlux(clientResponse -> this.handleResponseForFlux(clientResponse, new ParameterizedTypeReference<PagedModel<PersonResource>>() {
                }));

        return pagedModelFlux;

    }

    /**
     *
     * To make the handleResponseForMono method handle any type of return type in a Mono response,
     * use a generic method. This will allow us to pass the expected response type dynamically,
     * which can then be used to convert the body of the response to a Mono of the desired type.
     * @param response
     * @param responseType
     * @return
     * @param <T>
     */
    private <T>Mono<T> handleResponseForMono(ClientResponse response, Class<T> responseType) {

        if (response.statusCode().is2xxSuccessful()) {
            HttpStatusCode statusCode = response.statusCode();  // HTTP Status
            ClientResponse.Headers headers = response.headers();// HTTP Headers
            Mono<T> monoResponse = response.bodyToMono(responseType);  // Response Body
            log.info("WebClientCodeBufferPersonClient handleResponseForMono() Successful status : {}",statusCode.value());
            log.info("WebClientCodeBufferPersonClient handleResponseForMono() Header status : {}",headers);
            log.info("WebClientCodeBufferPersonClient handleResponseForMono() Response Body : {}",monoResponse);
            return monoResponse;
        }
        else if (response.statusCode().is4xxClientError()) {
            // Handle client errors (e.g., 404 Not Found)
            log.info("WebClientCodeBufferPersonClient handleResponseForMono() Client Error status : {}",response.statusCode().value());
            return Mono.error(new PersonNotFoundException("Person not found"));
        }
        else if (response.statusCode().is5xxServerError()) {
            // Handle server errors (e.g., 500 Internal Server Error)
            log.info("WebClientCodeBufferPersonClient handleResponseForMono() Server Side Error status : {}",response.statusCode().value());
            return Mono.error(new PersonCallingServerException("Server error"));
        }
        else {
            // Handle other status codes as needed
            log.info("WebClientCodeBufferPersonClient handleResponseForMono() Unexpected Error status : {}",response.statusCode().value());
            return Mono.error(new PersonCallingClientException("Unexpected error"));
        }
    }

    /**
     *
     * To make the handleResponseForFlux method handle any type of return type in a Flux response,
     * use a generic method. This will allow us to pass the expected response type dynamically,
     * which can then be used to convert the body of the response to a Flux of the desired type.
     * @param response
     * @param responseType
     * @return
     * @param <T>
     */

    private <T> Flux<T> handleResponseForFlux(ClientResponse response, Class<T> responseType) {

        if (response.statusCode().is2xxSuccessful()) {
            HttpStatusCode statusCode = response.statusCode();  // HTTP Status
            ClientResponse.Headers headers = response.headers();// HTTP Headers
            Flux<T> fluxResponse = response.bodyToFlux(responseType);  // Response Body
            log.info("WebClientCodeBufferPersonClient handleResponseForFlux() Successful status : {}",statusCode.value());
            log.info("WebClientCodeBufferPersonClient handleResponseForFlux() Header status : {}",headers);
            log.info("WebClientCodeBufferPersonClient handleResponseForFlux() Response Body : {}",fluxResponse);
            return fluxResponse; // Response Body
        }
        else if (response.statusCode().is4xxClientError()) {
            // Handle client errors (e.g., 404 Not Found)
            log.info("WebClientCodeBufferPersonClient handleResponseForFlux() Client Error status : {}",response.statusCode().value());
            return Flux.error(new PersonNotFoundException("Person not found"));
        }
        else if (response.statusCode().is5xxServerError()) {
            // Handle server errors (e.g., 500 Internal Server Error)
            log.info("WebClientCodeBufferPersonClient handleResponseForFlux() Server Side Error status : {}",response.statusCode().value());
            return Flux.error(new PersonCallingServerException("Server error"));
        }
        else {
            // Handle other status codes as needed
            log.info("WebClientCodeBufferPersonClient handleResponseForFlux() Unexpected Error status : {}",response.statusCode().value());
            return Flux.error(new PersonCallingClientException("Unexpected error"));
        }
    }

    /**
     *
     * Use ParameterizedTypeReference: Instead of passing Class<T>, you should use ParameterizedTypeReference
     * to specify PagedModel<PersonResource> in your handleResponseForFlux method. This will ensure you can deserialize the complex generic types.
     *
     * Using ParameterizedTypeReference: In the searchPerson method, replace Class<T> with ParameterizedTypeReference<PagedModel<PersonResource>>
     * to handle the generic types properly. This allows WebClient to map the response body into the correct type
     *
     * Avoid WrapperClass: The WrapperClass is unnecessary when using ParameterizedTypeReference,
     * which handles parameterized types natively.
     *
     * Optional.ofNullable() for Parameters: Instead of passing potentially null values directly,
     * Optional.ofNullable() is used for the query parameters, which helps avoid null checks.
     *
     * Handling Response in handleResponseForFlux: The handleResponseForFlux method now takes
     * ParameterizedTypeReference<T>, ensuring proper deserialization of the response body,
     * especially when dealing with generics like PagedModel<PersonResource>.
     *
     * This allows to search for PersonResource with pagination via WebClient and deserialize the response correctly into PagedModel<PersonResource>
     * @param response
     * @param responseType
     * @return
     * @param <T>
     */

    private <T> Flux<T> handleResponseForFlux(ClientResponse response, ParameterizedTypeReference<T> responseType) {
        if (response.statusCode().is2xxSuccessful()) {
            HttpStatusCode statusCode = response.statusCode();  // HTTP Status
            ClientResponse.Headers headers = response.headers();// HTTP Headers
            Flux<T> fluxResponse = response.bodyToFlux(responseType);  // Response Body
            log.info("WebClientCodeBufferPersonClient handleResponseForFlux() Successful status : {}",statusCode.value());
            log.info("WebClientCodeBufferPersonClient handleResponseForFlux() Header status : {}",headers);
            log.info("WebClientCodeBufferPersonClient handleResponseForFlux() Response Body : {}",fluxResponse);
            return fluxResponse; // Response Body
        }
        else if (response.statusCode().is4xxClientError()) {
            // Handle client errors (e.g., 404 Not Found)
            log.info("WebClientCodeBufferPersonClient handleResponseForFlux() Client Error status : {}",response.statusCode().value());
            return Flux.error(new PersonNotFoundException("Person not found"));
        }
        else if (response.statusCode().is5xxServerError()) {
            // Handle server errors (e.g., 500 Internal Server Error)
            log.info("WebClientCodeBufferPersonClient handleResponseForFlux() Server Side Error status : {}",response.statusCode().value());
            return Flux.error(new PersonCallingServerException("Server error"));
        }
        else {
            // Handle other status codes as needed
            log.info("WebClientCodeBufferPersonClient handleResponseForFlux() Unexpected Error status : {}",response.statusCode().value());
            return Flux.error(new PersonCallingClientException("Unexpected error"));
        }
    }

    private Mono<? extends Throwable> handleErrorResponse(HttpStatusCode statusCode) {
        // Handle non-success status codes here (e.g., logging or custom error handling)
        log.info("WebClientCodeBufferPersonClient handleErrorResponse() Error status : {}",statusCode);
        return Mono.error(new PersonCallingClientException("Failed to fetch Person. Status code: " + statusCode));
    }

    private Mono<? extends Throwable> handleClientError(ClientResponse clientResponse) {
        // Handle non-success status codes here (e.g., logging or custom error handling)
        log.info("WebClientCodeBufferPersonClient handleClientError() Error status : {}", clientResponse.statusCode());
        return Mono.error(new PersonNotFoundException("Failed to fetch Person. Person Not Found Status code: " + clientResponse.statusCode()));
    }

    private Mono<? extends Throwable> handleServerError(ClientResponse clientResponse) {
        // Handle non-success status codes here (e.g., logging or custom error handling)
        log.info("WebClientCodeBufferPersonClient handleServerError() Error status : {}", clientResponse.statusCode());
        return Mono.error(new PersonCallingServerException("Failed to fetch Person. Server Error Status code: " + clientResponse.statusCode()));
    }
}
