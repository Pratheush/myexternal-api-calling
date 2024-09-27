1. Maven
   To use WebClient api, we must have the spring-boot-starter-webflux module imported into our Spring Boot project

```
pom.xml :::
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```
2. Creating a Spring WebClient Instance
##### To create WebClient bean,
   Using WebClient.create() method is an overloaded method and can optionally accept a base URL for requests.
```
WebClient webClient = WebClient.create();  // With empty URI
WebClient webClient = WebClient.create("https://client-domain.com");  // With specified root URI
```
##### Using WebClient.Builder API
```
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
@Configuration
public class WebConfig {
  @Bean
  public WebClient webClient() {
    WebClient webClient = WebClient.builder()
      .baseUrl("http://localhost:3000")
      .defaultCookie("cookie-name", "cookie-value")
      .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
      .build();
  }
}
```

3. Using WebClient for Sending Requests and Handling Responses
   * Create WebClient.UriSpec reference using prebuilt methods such as get(), put(), post() or delete().
   * Set the request URI if not set already.
   * Set the request headers and authentication details, if any.
   * Set the request body, if any.
   * Call the retrieve() or exchange() method. The retrieve() method directly performs the HTTP request and retrieves the response body. The exchange() method returns ClientResponse having the response status and headers. We can get the response body from ClientResponse instance.
   * Handle the response returned from the server.

In the following example, we send an HTTP POST request to URI http://localhost:3000/employees that returns an Employee object after the successful call.
```
@Service
public class MyService {
  private final WebClient webClient;
  @Autowired
  public MyService(WebClient webClient) {
      this.webClient = webClient;
  }
  public Mono<Employee> createEmployee(Employee employee) {
    Mono<Employee> employeeMono = webClient.post()
      .uri("/employees")
      .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
      .body(Mono.just(employee), Employee.class)
      .retrieve()
      .bodyToMono(Employee.class);
    return employeeMono;
  }
}
```
Here’s an example of how we can use the service method and handle success and error scenarios:
```
public void someMethod() {
  Employee employee = new Employee(...); // Create an Employee instance
  myService.createEmployee(employee)
    .subscribe(
      createdEmployee -> {
        // Handle the successful response
        System.out.println("Employee created: " + createdEmployee);
      },
      error -> {
        // Handle errors
        System.err.println("Error creating employee: " + error.getMessage());
      }
    );
}
```
4. WebClient retrieve() vs. exchange() APIs
   Since Spring 5.3, the exchange() method is deprecated due to potential memory and connection leaks. Prefer exchangeToMono(), exchangeToFlux(), or retrieve() instead.

##### Using retrieve() API
The retrieve() is for common use cases where we want to send an HTTP request, receive the response, and handle it in a reactive way.

When we call retrieve(), the request is sent, and the response is automatically processed and deserialized into a reactive type (e.g., Mono or Flux). We don’t need to explicitly subscribe to the response.

The response type is inferred from the final call to bodyToMono() or bodyToFlux(). For example, if we use bodyToMono(Employee.class), we’ll get a Mono<Employee> as the result.
```
Mono<Employee> employeeMono = webClient.get()
    .uri("/employees/{id}", 123)
    .retrieve()
    .bodyToMono(Employee.class);
```
Please note that bodyToMono() and bodyToFlux() methods always expect a response body of a given class type. If the response status code is 4xx (client error) or 5xx (Server error) i.e. there is no response body then these methods throw WebClientException.

Use bodyToMono(Void.class) if no response body is expected. This is helpful in DELETE operations.
```
No response body is expectedwebClient.delete()
  .uri("/employees/" + id)
  .retrieve()
  .bodyToMono(Void.class);
```

##### Using exchange() API
The exchange() API allows us to handle the request and response explicitly. It returns the ClientResponse which has all the response elements such as status, headers and response body as well.

With exchange(), we are responsible for subscribing to the response explicitly using subscribe(), block() or similar methods. This gives us more control over when and how the request is executed.

When using exchange(), we must always use any of the bodyToMono(), bodyToFlux() or toEntity() methods of ClientResponse which provides more flexibility in choosing the reactive type for the response.

```
Mono<ClientResponse> responseMono = webClient.get()
    .uri("/employees/{id}", 123)
    .exchange();
responseMono.subscribe(clientResponse -> {
  HttpStatus statusCode = clientResponse.statusCode();  // HTTP Status
  HttpHeaders headers = clientResponse.headers();  // HTTP Headers
  Mono<Employee> employeeMono = clientResponse.bodyToMono(Employee.class);  // Response Body
  // Handle the response, including error handling based on status code
});
```
Here’s an example of how to use exchangeToMono() to make a GET request with Spring WebClient and handle the response:
```
Use exchangeToMono() or exchangeToFlux()

@Service
public class MyService {
  private final WebClient webClient;
  public MyService(WebClient webClient) {
      this.webClient = webClient;
  }
  public Mono<Employee> fetchEmployeeById(int id) {
    return webClient.get()
      .uri("/employees/{id}", id)
      .exchangeToMono(this::handleResponse);
  }
  private Mono<Employee> handleResponse(ClientResponse response) {
    if (response.statusCode().is2xxSuccessful()) {
      return response.bodyToMono(Employee.class);
    }
    else if (response.statusCode().is4xxClientError()) {
      // Handle client errors (e.g., 404 Not Found)
      return Mono.error(new EmployeeNotFoundException("Employee not found"));
    }
    else if (response.statusCode().is5xxServerError()) {
      // Handle server errors (e.g., 500 Internal Server Error)
      return Mono.error(new RuntimeException("Server error"));
    }
    else {
      // Handle other status codes as needed
      return Mono.error(new RuntimeException("Unexpected error"));
    }
  }
}
```


### Spring WebFlux configures the default memory limit for buffering data in-memory to 256KB. If this limit is exceeded in any case then we will encounter DataBufferLimitException error. To reset the memory limit, configure the below property in application.properties file.
```
application.properties ::::

spring.codec.max-in-memory-size=1MB
```

### Configuring Connection Timeouts
use Apache HttpClient class to set timeout periods for connection timeout, read timeout and write timeouts.
```
@Bean
public WebClient getWebClient() {
  HttpClient httpClient = HttpClient.create()
    .tcpConfiguration(client ->
        client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
        .doOnConnected(conn -> conn
            .addHandlerLast(new ReadTimeoutHandler(10))
            .addHandlerLast(new WriteTimeoutHandler(10))));
  ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
  return WebClient.builder()
    .baseUrl("http://localhost:3000")
    .clientConnector(connector)
    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    .build();
}
```

***
***

```
public Mono<User> getUserById(int id, String includeFields) {
  return webClient.get()
    .uri(uriBuilder -> uriBuilder
        .path("/api/users/{id}")
        .queryParam("includeFields", includeFields)
        .build(id))
    .header(HttpHeaders.AUTHORIZATION, "Bearer your-token")  // Add headers
    .cookie("sessionId", "your-session-id")  // Add cookies
    .retrieve()
    .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
        clientResponse.bodyToMono(String.class)
            .flatMap(body -> Mono.error(new RuntimeException("Client Error: " + body))))
    .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
        clientResponse.bodyToMono(String.class)
            .flatMap(body -> Mono.error(new RuntimeException("Server Error: " + body))))
    .bodyToMono(User.class)  // Handle response and map to User class
    .doOnError(WebClientResponseException.class, e -> {
      // Handle error and log it
      System.err.println("Error occurred: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
    });
}
```

#### Making Synchronous GET Requests
the block() method waits for the response, making the call synchronous.
```
private WebClient webClient;  // Inject using constructor
User user = webClient
    .get()
    .uri("/api/users/{id}", 123)
    .accept(MediaType.APPLICATION_JSON)
    .retrieve()
    .bodyToMono(User.class)
    .block(); // Blocks until the response is available
System.out.println("Response: " + user);
```

We can use the toEntity() method to access the API response as ResponseEntity. This allows us to access the response code, headers, and other helpful information.
```
private WebClient webClient;
ResponseEntity<User> userEntity = webClient
    .get()
    .uri("/api/users/{id}", 123)
    .accept(MediaType.APPLICATION_JSON)
    .retrieve()
    .toEntity(User.class)   // Fetch response entity
    .block();	// Blocks until the response is available
```

To get the list of users, we have two options:
If the remote API returns is synchronous, we can use ParameterizedTypeReference with List<User> type. List<User> is generic type
```
private WebClient webClient;
List<User> users = webClient
    .get()
    .uri("/api/users")
    .accept(MediaType.APPLICATION_JSON)
    .retrieve()
    .bodyToMono(new ParameterizedTypeReference<List<User>>() {})
    .block();
```

As a best practice, please avoid using bodyToFlux() and block() methods together as it defeats the whole purpose of asynchronous processing.

If the remote API returns is asynchronous, we can use ‘.bodyToFlux().collectList()…’ method calls.
```
private WebClient webClient;
List<User> users = webClient
    .get()
    .uri("/api/users/{id}", 123)
    .accept(MediaType.APPLICATION_JSON)
    .retrieve()
    .bodyToFlux(User.class)
    .collectList()
    .block();
```

#### Making Asynchronous GET Requests
Asynchronous requests do not block the calling thread and allow other tasks to proceed. This is the recommended approach in most of the cases.

We can make a call asynchronous simply by not blocking it using the block() method. 

Here, we have two options to handle the response:

Return the Mono or the Flux response to the asynchronous client and let it handle the response status and body.
```
public Mono<User> getUserById(int id) {
  return webClient
    .get()
    .uri("/api/users/{id}", id)
    .retrieve()
    .bodyToMono(User.class)
    .onStatus(HttpStatus::is4xxClientError, response -> {
        return response.bodyToMono(String.class)
            .flatMap(body -> Mono.error(new RuntimeException("Client Error: " + body)));
    })
    .onStatus(HttpStatus::is5xxServerError, response -> {
        return response.bodyToMono(String.class)
            .flatMap(body -> Mono.error(new RuntimeException("Server Error: " + body)));
    });
}
```
Subscribe to the response, and handle it when it arrives.

```
public void handleUserRequest() {
  return webClient
    .get()
    .uri("/api/users/{id}", id)
    .retrieve()
    .bodyToMono(User.class)
    .subscribe(
      user -> System.out.println("User: " + user),		// Handle success response
      error -> System.err.println("Error: " + error.getMessage())	//handle error
  );
}
```

## Advanced Use Cases

####  Adding Query Parameters
Use queryParam() method to add query parameters:

```
String fields = "id,name";
Mono<List<User>> userList = webClient
  .get()
  .uri(uriBuilder -> uriBuilder
      .uri("/api/users/{id}", id)
      .queryParam("fields", fields)
      .build())
  .retrieve()
  .bodyToFlux(User.class)
  .collectList();
```
#### Adding Headers and Cookies
To add headers and cookies, use the methods headers() and cookies() and pass the names and values.
For example, in the following code, we are passing the OAuth2 token in the AUTHORIZATION header and a cookie sessionId with its value.

```
public Mono<User> getUserById(int id) {
  return webClient
    .get()
    .uri("/api/users/{id}", id)
    .headers(headers -> headers.set(HttpHeaders.AUTHORIZATION, "Bearer token"))
    .cookie("sessionId", "123456789")
    .retrieve()
    .bodyToMono(User.class);
}
```

#### Handling Pagination and Streaming Data
To enable pagination and handle the streaming data from an async data source, we can pass the paging parameters in the request and receive the response as Flux.
```
public Flux<User> getPaginatedUsers(int page, int size) {
  return webClient
    .get()
    .uri(uriBuilder -> uriBuilder
        .path("/api/users")
        .queryParam("page", page)
        .queryParam("size", size)
        .build())
    .retrieve()
    .bodyToFlux(User.class);
}
```

#### Timeout and Retry Strategies
To handle the timeout, pass the timeout duration in timeout() method. 
Note that we can also set the timeout, globally, by configuring in WebClient bean configuration as well.

Also, we can use the retryWhen() method to set the number of retries before concluding the request failure.

```
public Mono<User> getUserById(int id) {
  return webClient
    .get()
    .uri("/api/users/{id}", id)
    .retrieve()
    .bodyToMono(User.class)
    .timeout(Duration.ofSeconds(5))
    .retryWhen(Retry.backoff(3, Duration.ofSeconds(10)));
}
```
***
***

### Error Handling
We can use the following methods for adding the error handling code in WebClient calls:

| Method          | Description                                                                                                              |
|-----------------|--------------------------------------------------------------------------------------------------------------------------|
| onStatus()      | Handle specific HTTP status codes and define custom behavior based on the status code of the HTTP response.              |
| onRawStatus()   | Provides a more granular approach to handle HTTP status codes, allowing for custom behavior based on exact status codes. |
| doOnError()     | Perform an action when an error is detected, such as logging or performing side effects.                                 |
| onErrorResume() | Provides a fallback mechanism by allowing a default value or alternative handling when an error occurs.                  |
                                                                                                           

```
User user = webClient
    .get()
    .uri("/api/users/{id}", 123)
    .accept(MediaType.APPLICATION_JSON)
    .retrieve()
    .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
        clientResponse.bodyToMono(String.class)
            .flatMap(errorBody -> Mono.error(new RuntimeException("Client Error: " + errorBody))))
    .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
        clientResponse.bodyToMono(String.class)
            .flatMap(errorBody -> Mono.error(new RuntimeException("Server Error: " + errorBody))))
    .bodyToMono(User.class)   // Fetch response entity
    .doOnError(throwable -> log.warn("Error when issuing request :: ", throwable))
    .block();
```

### Testing WebClient GET Requests
Testing WebClient GET requests involves using the class WebClientTest for unit tests or mocking libraries like Mockito.
The @WebFluxTest(UserController.class) sets up a WebTestClient for testing the UserController without starting a full HTTP server.

In the following test, we perform a GET request to ‘/api/users/123‘, check that the status is OK, and assert that the response body matches the expected User details.
```
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.beans.factory.annotation.Autowired;
@WebFluxTest(UserController.class)
public class UserControllerTest {
  @Autowired
  private WebTestClient webTestClient;
  @Test
  void testGetUserById() {
    webTestClient.get()
      .uri("/api/users/123")
      .exchange()
      .expectStatus().isOk()
      .expectBody(User.class)
      .isEqualTo(new User(123, "John Doe"));
  }
}
```
Here, the UserController is a simple class for testing purposes:
```
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
@RestController
public class UserController {
  @GetMapping("/api/users/{id}")
  public Mono<User> getUserById(@PathVariable int id) {
    // Fetch the user from a database
    User user = new User(id, "John Doe");
    return Mono.just(user);
  }
}
```

```
WebTestClient
    .bindToServer()
      .baseUrl("http://localhost:8080")
      .build()
      .post()
      .uri("/resource")
    .exchange()
      .expectStatus().isCreated()
      .expectHeader().valueEquals("Content-Type", "application/json")
      .expectBody().jsonPath("field").isEqualTo("value");
```































