### RestTemplate is older, synchronous API for making HTTP requests.
### WebClient is reactive, non-blocking client part of Spring WebFlux
### RestClient is the new addition to Spring framework

The RestClient is part of the Spring Web module so include it in the application. It is available in Spring Framework 6.1 onwards.

```
<dependency>
  <groupId>org.springframework</groupId>
  <artifactId>spring-web</artifactId>
  <version>6.1</version>
  <scope>compile</scope>
</dependency>
```
In Spring boot applications, we can transitively include all the necessary dependencies with the web starter dependency.

```
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```
If we are using the HttpClient for underlying HTTP access, we need to add those dependencies additionally.
```
<dependency>
  <groupId>org.apache.httpcomponents.client5</groupId>
  <artifactId>httpclient5</artifactId>
  <version>5.2.1</version>
</dependency>
```
### Creating RestClient
```
import org.springframework.web.client.RestClient;
//...
@Value("${REMOTE_BASE_URI:http://localhost:3000}")
String baseURI;
@Bean
RestClient restClient() {
  return RestClient.create(baseURI);
}
```
We can also use the builder() method which allows us to set more complex options such as default headers, request processors, message handlers etc.
```
@Autowired
CloseableHttpClient httpClient;

@Bean
RestClient restClient() {
  return RestClient.builder()
      .baseUrl(baseURI)
      //.requestInterceptor(...)
      //.defaultHeader("AUTHORIZATION", fetchToken())
      //.messageConverters(...)
      .requestFactory(clientHttpRequestFactory())
      .build();
}

@Bean
public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
  HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
  	= new HttpComponentsClientHttpRequestFactory();
  clientHttpRequestFactory.setHttpClient(httpClient);
  return clientHttpRequestFactory;
}

```
in an existing application, where we have been using RestTemplate for HTTP communication, we can reuse HTTP configuration in RestClient bean.
```
@Bean
RestClient restClient() {
	return RestClient.create(restTemplate());
}
```

#### HTTP POST
```
Employee newEmployee = new Employee(5l, "Amit", "active");
ResponseEntity<Void> responseEntity = restClient.post()
    .uri("/employees")
    .contentType(MediaType.APPLICATION_JSON)
    .body(newEmployee)
    .retrieve()
    .toBodilessEntity();
Assertions.assertEquals(HttpStatus.CREATED.value(), responseEntity.getStatusCode().value());
Assertions.assertEquals("http://localhost:3000/employees/5",
	responseEntity.getHeaders().get("Location").get(0));
```

#### HTTP PUT
```
ResponseEntity<Employee> responseEntity = restClient.put()
    .uri("/employees/1")
    .contentType(MediaType.APPLICATION_JSON)
    .accept(MediaType.APPLICATION_JSON)
    .body(updatedEmployee)
    .retrieve()
    .toEntity(Employee.class);
Assertions.assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
Assertions.assertEquals(updatedEmployee.getName(), responseEntity.getBody().getName());
```

####  HTTP GET
The restClient.get() is used to create a GET request to the specified URL. Note that we can pass the dynamic values to URI templates.
```
restClient.get()
	.uri("/employees")
	//...
restClient.get()
	.uri("/employees/{id}", id)
	//...
```
```
List<Employee> employeeList = restClient.get()
    .uri("/employees")
    .accept(MediaType.APPLICATION_JSON)
    .retrieve()
    .body(List.class);
Assertions.assertNotNull(employeeList);
```

If we are interested in handling the other response parts, such as status, headers etc, we can fetch the entity as ResponseEntity as follows:
```
ResponseEntity<List> responseEntity = restClient.get()
    .uri("/employees")
    .accept(MediaType.APPLICATION_JSON)
    .retrieve()
    .toEntity(List.class);
Assertions.assertNotNull(responseEntity.getBody());
Assertions.assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
Assertions.assertNotEquals(null, responseEntity.getHeaders());

```
####  RestClient exchange() API
If we need complete control over the response processing, we can employ the exchange() method. 
It gives access to HttpRequest and HttpResponse objects and then we can use them the way we require.

```
List<Employee> list = restClient.get()
    .uri("/employees")
    .accept(MediaType.APPLICATION_JSON)
    .exchange((request, response) -> {
      List apiResponse = null;
      if (response.getStatusCode().is4xxClientError()
          || response.getStatusCode().is5xxServerError()) {
        Assertions.fail("Error occurred in test execution. Check test data and api url.");
      } else {
        ObjectMapper mapper = new ObjectMapper();
        apiResponse = mapper.readValue(response.getBody(), List.class);
      }
      return apiResponse;
    });
Assertions.assertEquals(4, list.size());
```

#### Exception Handling
The RestClient throws two types of exceptions for a failed request:
* HttpClientErrorException: with 4xx response code
* HttpServerErrorException: with 5xx response code
```
HttpClientErrorException thrown = Assertions.assertThrows(HttpClientErrorException.class,
  () -> {
    Employee employee = restClient.get()
      .uri("/employees/500")
      .accept(MediaType.APPLICATION_JSON)
      .retrieve()
      .body(Employee.class);
  });
Assertions.assertEquals(404, thrown.getStatusCode().value());
```


My onStatus Exception Handling :::
```
public PagedModel<PersonResource> searchPerson(Optional<String> name, Optional<Integer> minAge,
                                                   Optional<Integer> maxAge, Optional<String> city,
                                                   Optional<Integer> page, Optional<Integer> size) {
        log.info("RestClientCodeBufferPersonClient SearchPerson called");

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .query("/search") // try to use .query() and .path()
                        .queryParamIfPresent("name",name)
                        .queryParamIfPresent("min",minAge)
                        .queryParamIfPresent("max",maxAge)
                        .queryParamIfPresent("city",city)
                        .queryParamIfPresent("page",page)
                        .queryParamIfPresent("size",size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
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
                .body(new ParameterizedTypeReference<PagedModel<PersonResource>>() {});
    }
```

























































































