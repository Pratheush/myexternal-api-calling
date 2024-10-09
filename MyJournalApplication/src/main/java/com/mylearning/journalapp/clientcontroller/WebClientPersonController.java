package com.mylearning.journalapp.clientcontroller;

import com.mylearning.journalapp.client.WebClientCodeBufferPersonClient;
import com.mylearning.journalapp.clientconfig.InterceptorContext;
import com.mylearning.journalapp.clientresponse.JWTAuthResponse;
import com.mylearning.journalapp.clientresponse.LoginDto;
import com.mylearning.journalapp.clientresponse.Person;
import com.mylearning.journalapp.clientresponse.PersonResource;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/web-client-person")
@Slf4j
@ConditionalOnBean(WebClientCodeBufferPersonClient.class)  //Register a bean only if another bean is already registered. Example: Combine with another condition
public class WebClientPersonController {

    private final WebClientCodeBufferPersonClient webClientPersonClient;

    public WebClientPersonController(WebClientCodeBufferPersonClient webClientPersonClient) {
        this.webClientPersonClient = webClientPersonClient;
    }

    @PostMapping("/login")
    public ResponseEntity<JWTAuthResponse> login(@RequestBody LoginDto loginDto) {
        log.info("WebClientPersonController login() called");
        try {
            // Set the Context Flag in the Controller Method:
            InterceptorContext.setDisableInterceptor(true); // Disable interceptor for login
            JWTAuthResponse jwtAuthResponse = webClientPersonClient.login(loginDto);
            return ResponseEntity.ok(jwtAuthResponse);
        }  finally {
            log.info("WebClientPersonController login finally Block");
            InterceptorContext.clear(); // Clear the context
        }
    }

    //================================================================================================================

    @GetMapping("/population-by-city")
    public ResponseEntity<Flux<Document>> getPopulationByCity(){
        Flux<Document> documentFlux = webClientPersonClient.getPopulationByCity();
        return ResponseEntity.ok(documentFlux);
    }

    /**
     *
     * doOnSubscribe and doOnError are used to add side effects (logging in this case) at specific points
     * in the lifecycle of the reactive stream represented by Flux<Document>.
     *
     * Purpose: This method is called when a subscription is made to the reactive stream. It allows you
     * to execute a side effect when the stream is subscribed to.
     *
     * Usage: In this case, doOnSubscribe is used to log information indicating that a subscription has occurred,
     * along with the oldestByCity parameter.
     *
     * When It Triggers: This code will execute immediately when the subscription starts, before any data is emitted.
     * use this to log when a request to retrieve data starts, which can help with tracing and debugging the flow of your application.
     *----------------------------------------------------------------
     * doOnError()
     * Purpose: This method is called when an error occurs during the execution of the reactive stream.
     * It allows you to handle errors gracefully and perform actions, such as logging.
     *
     * This code will execute only if an error happens during the execution of the stream
     *==============================================================
     *
     * doOnNext()
     * Purpose: It is used to execute a side effect whenever an item is emitted by the upstream publisher.
     * Usage: You can use doOnNext() to log, modify, or perform any action with the emitted item without altering the stream itself.
     *
     * Validation: To perform validations on each emitted item before further processing.
     * Transformation: While you shouldn't modify the stream itself in doOnNext(),
     * you can perform actions that might be necessary based on the emitted item (e.g., updating a counter).
     *
     * @return
     */
    @GetMapping("/oldest-person-by-city")
    public ResponseEntity<Flux<Document>> getOldestPersonByCity(){
        Flux<Document> oldestPersonByCity = webClientPersonClient.getOldestPersonByCity()
                .doOnSubscribe(oldestByCity -> log.info("WebClientPersonController Oldest person by city {}", oldestByCity))  // Handle the successful response
                .doOnError(error -> log.error("WebClientPersonController error occurred while retrieving oldest person by city {}", error.getMessage())); // Handle errors
        return ResponseEntity.ok(oldestPersonByCity);
    }

    @GetMapping("/person-by-id/{personId}")
    public ResponseEntity<Mono<Person>> getPersonById(@PathVariable ObjectId personId){
        Mono<Person> monoPersonById = webClientPersonClient.getPersonById(personId);
        // Process the Person retrieved by ID
        monoPersonById.subscribe(person ->log.info("WebClientPersonController getPersonById Person By Id {}", person));
        return ResponseEntity.ok(monoPersonById);
    }

    @GetMapping("/all-person")
    public ResponseEntity<Flux<Person>> getAllEmployees(){
        Flux<Person> fluxPerson = webClientPersonClient.getAllPersons();
        // Process each Person in the Flux
        fluxPerson.subscribe(person ->log.info("WebClientPersonController getAllEmployees All Person {}", person));
        return ResponseEntity.ok(fluxPerson);
    }

    @PostMapping("/subscribe-without-response-error-handling")
    public void createPersonWithoutResponseHandling(@RequestBody Person person) {
        webClientPersonClient.createPersonWithoutResponseHandling(person)
                .subscribe(
                        // Handle the successful response
                        createdPerson -> log.info("WebClientPersonController Person Created : {}", createdPerson),
                        // Handle errors
                        error -> log.error("WebClientPersonController Error Creating Person : {}", error.getMessage())
                );
    }

    /**
     * subscribe() is a method used to initiate the execution of a reactive stream (like Mono or Flux).
     *
     * Asynchronous Behavior: Using subscribe() allows the operation to remain non-blocking.
     * The calling thread can continue executing without waiting for the result.
     *
     * Handling Responses: The provided lambdas will handle the emitted createdPerson or any error that might occur during the operation.
     *
     * When to Use subscribe():
     * Asynchronous Operations: When you want to perform actions based on the results of a reactive operation without blocking.
     * Event Handling: In event-driven applications where you want to react to data as it becomes available.
     * @param person
     */
    @PostMapping("/subscribe-with-response-error-handling")
    public void createPersonWithResponseHandling(@RequestBody Person person) {
        webClientPersonClient.createPersonWithResponseHandling(person)
                .subscribe(
                        // Handle the successful response
                        createdPerson -> log.info("WebClientPersonController Person Created : {}", createdPerson),
                        // Handle errors
                        error -> log.error("WebClientPersonController Error Creating Person : {}", error.getMessage())
                );
    }

    /**
     *
     * The block() method is used to synchronously wait for the completion of a reactive stream (like Mono or Flux).
     * When you call block(), it will block the current thread until the reactive operation is complete
     * and return the result. This is contrary to the non-blocking nature of reactive programming,
     * where operations usually execute asynchronously.
     *
     * block() Does:
     * Waits for the Completion: block() will block the calling thread until the reactive stream completes.
     *
     * Returns the Result: After the completion of the reactive stream, it returns the value emitted
     * by the Mono or Flux. If the operation results in an error, it throws the exception.
     *
     *  Using block() in a reactive web application like WebFlux is discouraged because it negates the benefits of asynchronous programming by blocking the thread.
     *
     * @param person
     * @return
     */
    @PostMapping("/create-person-on-status")
    public ResponseEntity<Person> createPersonOnStatus(@RequestBody Person person){
        return webClientPersonClient.createPersonOnStatus(person)
                .log().block();
    }

    @PutMapping
    public ResponseEntity<Mono<Person>> updatePerson(@RequestBody Person person, @RequestParam ObjectId personId){
        Mono<Person> personMono = webClientPersonClient.updatePerson(person, personId);
        return ResponseEntity.status(HttpStatus.OK).body(personMono);
    }

    @DeleteMapping("/body-to-mono/{personId}")
    public ResponseEntity<Mono<Void>> deletePersonBodyToMono(@PathVariable ObjectId personId){
        Mono<Void> voidMono = webClientPersonClient.deletePersonBodyToMono(personId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(voidMono);
    }

    @DeleteMapping("/to-entity/{personId}")
    public ResponseEntity<Mono<Void>> deletePersonToEntity(@PathVariable ObjectId personId){
        Mono<Void> voidMono = webClientPersonClient.deletePersonToEntity(personId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(voidMono);
    }

    @GetMapping("/search-person")
    public ResponseEntity<Flux<PagedModel<PersonResource>>> searchPerson(
            @RequestParam(required = false) String name,
            @RequestParam(value = "min",required = false) Integer minAge,
            @RequestParam(value = "max",required = false) Integer maxAge,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size
    ){
        log.info("WebClientPersonController searchPerson() called");
        Flux<PagedModel<PersonResource>> fluxPagedModelPersonResource = webClientPersonClient.searchPerson(Optional.ofNullable(name), Optional.ofNullable(minAge), Optional.ofNullable(maxAge), Optional.ofNullable(city),
                Optional.ofNullable(page), Optional.ofNullable(size));
        return ResponseEntity.status(HttpStatus.OK).body(fluxPagedModelPersonResource);
    }

    @GetMapping("/search-person-using-handle-response-for-flux")
    public ResponseEntity<Flux<PagedModel<PersonResource>>> searchPersonUsingHandleResponseForFlux(
            @RequestParam(required = false) String name,
            @RequestParam(value = "min",required = false) Integer minAge,
            @RequestParam(value = "max",required = false) Integer maxAge,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size
    ){
        log.info("WebClientPersonController searchPersonUsingHandleResponseForFlux() called");
        Flux<PagedModel<PersonResource>> pagedModelFlux = webClientPersonClient.searchPersonUsingHandleResponseForFlux(Optional.ofNullable(name), Optional.ofNullable(minAge), Optional.ofNullable(maxAge), Optional.ofNullable(city),
                Optional.ofNullable(page), Optional.ofNullable(size));

        return ResponseEntity.status(HttpStatus.OK).body(pagedModelFlux);
    }
}
