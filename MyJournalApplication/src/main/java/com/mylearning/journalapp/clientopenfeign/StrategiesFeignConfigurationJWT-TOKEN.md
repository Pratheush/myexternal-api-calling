
## STRATEGIES To isolate the behavior of a RequestInterceptor in OpenFeign so that it only applies to specific requests (e.g., exclude it for login requests while applying it to other requests), TO USE AUTHORIZATION HEADER FOR JWT-TOKEN

### Strategy 1: Use Custom Feign Configuration for Specific Feign Clients
Spring provides a way to create separate configurations for each Feign client. You can define multiple configurations and apply them selectively to different Feign clients. This approach allows you to enable or disable interceptors based on the specific Feign client configuration.
1. Create a Custom Feign Configuration without the Interceptor
   Create a Feign configuration class excluding the interceptor for the login client:
```
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import feign.RequestInterceptor;

@Configuration
public class FeignLoginConfiguration {

    @Bean
    public RequestInterceptor noAuthRequestInterceptor() {
        return template -> {
            // No Authorization Header for login
        };
    }
}

```
2. Create Another Feign Configuration with the Interceptor
   Create another configuration for other Feign clients that includes the JWT Authorization:
```
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import feign.RequestInterceptor;

@Configuration
public class FeignAuthConfiguration {

    @Bean
    public RequestInterceptor authRequestInterceptor() {
        return template -> {
            String jwtToken = "Bearer " + getJwtToken();
            template.header("Authorization", jwtToken);
        };
    }

    private String getJwtToken() {
        // Your logic to fetch JWT token (e.g., from a secure storage or static variable)
        return "your-jwt-token";
    }
}

```
3. Apply the Configurations to Different Feign Clients
   In your Feign client interfaces, specify which configuration to use for each Feign client:
   Feign Client for Login (No Interceptor):
```
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "loginClient", url = "http://localhost:8081/api/person", configuration = FeignLoginConfiguration.class)
public interface LoginFeignClient {

    @PostMapping("/login")
    JWTAuthResponse login(@RequestBody LoginDto loginDto);
}

```
   Feign Client for Other Requests (With Interceptor):
```
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "personClient", url = "http://localhost:8081/api/person", configuration = FeignAuthConfiguration.class)
public interface PersonFeignClient {

    @GetMapping("/populationByCity")
    PopulationResponse getPopulationByCity(@RequestParam("city") String city);
}

```
This way, the LoginFeignClient will not have the Authorization header, whereas the PersonFeignClient will include the header for all other requests.

### Strategy 2: Use Conditional Logic Inside the RequestInterceptor
If you want a more dynamic approach that doesn't require separate configurations, you can include conditional logic in a single RequestInterceptor to determine whether or not to add the Authorization header based on the specific request URL, HTTP method, or request headers.
1. Create a Single Feign RequestInterceptor with Conditional Logic
   Implement your RequestInterceptor such that it checks for the request URL or headers before adding the Authorization header:
```
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class ConditionalAuthInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // If the request is for the login endpoint, skip adding the Authorization header
        if (template.url().contains("/login")) {
            return; // Skip the interceptor for login request
        }

        // For other endpoints, add the Authorization header
        String jwtToken = "Bearer " + getJwtToken();
        template.header("Authorization", jwtToken);
    }

    private String getJwtToken() {
        // Your logic to fetch JWT token (e.g., from a secure storage or static variable)
        return "your-jwt-token";
    }
}

```
2. Register the Interceptor Globally
   This interceptor will be applied to all Feign clients, but the conditional logic will determine whether to skip or include the Authorization header for the login endpoint.

### Strategy 3: Use Feign Request Headers Directly in the Controller or Service Method
If your requirement is limited to only some methods, you can set the Authorization header in the Feign client method using the @RequestHeader annotation.
we can selectively add the JWT token for specific methods:
```
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "personClient", url = "http://localhost:8081/api/person")
public interface PersonFeignClient {

    @GetMapping("/populationByCity")
    PopulationResponse getPopulationByCity(@RequestParam("city") String city,
                                           @RequestHeader("Authorization") String authorizationHeader);
}

```
we can then call this method from your service or controller and pass the Authorization header dynamically:
```
@Autowired
private PersonFeignClient personFeignClient;

public PopulationResponse getPopulation(String city) {
    String jwtToken = "Bearer " + getJwtToken();
    return personFeignClient.getPopulationByCity(city, jwtToken);
}

private String getJwtToken() {
    // Your logic to fetch JWT token
    return "your-jwt-token";
}

```
we can manually add the header for specific method calls and skip it for others.

### Strategy 4: Use a ThreadLocal or Context Variable to Control Interceptor Behavior
If you want more flexibility, use a ThreadLocal variable or a context holder to control whether the RequestInterceptor should apply the Authorization header based on the calling method.
1. Create a Context Class to Store the Flag:
```
public class FeignContextHolder {
    private static final ThreadLocal<Boolean> skipAuthorization = ThreadLocal.withInitial(() -> false);

    public static void setSkipAuthorization(boolean value) {
        skipAuthorization.set(value);
    }

    public static boolean isSkipAuthorization() {
        return skipAuthorization.get();
    }

    public static void clear() {
        skipAuthorization.remove();
    }
}

```
2. Modify the Interceptor to Check This Flag:
```
@Component
public class ConditionalAuthInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // Check the ThreadLocal context to see if Authorization should be skipped
        if (FeignContextHolder.isSkipAuthorization()) {
            return; // Skip adding Authorization header
        }

        // Otherwise, add the Authorization header
        String jwtToken = "Bearer " + getJwtToken();
        template.header("Authorization", jwtToken);
    }

    private String getJwtToken() {
        return "your-jwt-token";
    }
}

```
3. Set the Context Flag in the Controller Method:
   Before calling the Feign client for the login request, set the flag:
```
@PostMapping("/login")
public ResponseEntity<JWTAuthResponse> login(@RequestBody LoginDto loginDto) {
    try {
        FeignContextHolder.setSkipAuthorization(true);  // Disable interceptor for login
        JWTAuthResponse response = loginFeignClient.login(loginDto);
        return ResponseEntity.ok(response);
    } finally {
        FeignContextHolder.clear(); // Clear the context
    }
}

```
Summary::
* Use Custom Feign Configurations to define different behaviors for different clients.
* Implement Conditional Logic within a single RequestInterceptor to apply headers selectively.
* Use Method-Level Headers to control header inclusion manually using @RequestHeader.
* Use a ThreadLocal or Context Variable to toggle interceptor behavior dynamically.

***
***

## STRATEGIES To isolate the behavior of a RequestInterceptor in WEB-CLIENT so that it only applies to specific requests (e.g., exclude it for login requests while applying it to other requests), TO USE AUTHORIZATION HEADER FOR JWT-TOKEN

### Approach 1: Using .filter() Directly in WebClient for Specific Requests
we can define the interceptor in your WebClient bean and selectively apply it:
```
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(JwtInterceptor jwtInterceptor) {

        return WebClient.builder()
                .filter((request, next) -> {
                    // Check if the request is a login request
                    if (request.url().toString().contains("/login")) {
                        return next.exchange(request); // Skip the JWT interceptor for login
                    }

                    return jwtInterceptor.intercept(request, next);
                })
                .build();
    }
}

```
Here, the interceptor is conditionally applied based on the URL. Modify the condition as needed (e.g., using request headers or path patterns).

### Approach 2: Define Multiple WebClients with Different Configurations
1.  If the above approach is not feasible, we can define multiple WebClient beans in your configuration, each with a specific set of interceptors:
```
@Bean("loginClient")
public WebClient loginWebClient() {
    return WebClient.builder().build(); // No interceptor for login
}

@Bean("securedClient")
public WebClient securedWebClient(JwtInterceptor jwtInterceptor) {
    return WebClient.builder()
            .filter(jwtInterceptor)  // interceptor for login
            .build();
}

```
2. Then, inject the appropriate WebClient in the corresponding service methods:

```
@Autowired
@Qualifier("loginClient")
private WebClient loginWebClient;

@Autowired
@Qualifier("securedClient")
private WebClient securedWebClient;

```

### Approach: Using a ThreadLocal to Dynamically Control the Interceptor
A more dynamic approach, consider using a ThreadLocal variable in your interceptor to control its behavior based on the calling context:
1. Create a ThreadLocal variable to store the flag:
```
public class InterceptorContext {
    private static final ThreadLocal<Boolean> disableInterceptor = ThreadLocal.withInitial(() -> false);

    public static void setDisableInterceptor(boolean value) {
        disableInterceptor.set(value);
    }

    public static boolean isInterceptorDisabled() {
        return disableInterceptor.get();
    }

    public static void clear() {
        disableInterceptor.remove();
    }
}

```
2. Modify your interceptor to check this flag:
```
public class JwtInterceptor implements ExchangeFilterFunction {

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        if (InterceptorContext.isInterceptorDisabled()) {
            return next.exchange(request); // Skip if disabled
        }

        ClientRequest newRequest = ClientRequest.from(request)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getJwtToken())
                .build();
        return next.exchange(newRequest);
    }

    private String getJwtToken() {
        // Fetch your JWT token here
        return "your-jwt-token";
    }
}

```
3. In your controller method, use the InterceptorContext to disable the interceptor:
```
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
    InterceptorContext.setDisableInterceptor(true); // Disable interceptor for login
    ResponseEntity<?> response = loginService.login(loginDto);
    InterceptorContext.clear(); // Clear the context
    return response;
}

```
This way, you can dynamically enable or disable the interceptor for specific method calls.

Summary::
* Conditional Bean Creation: Use @Conditional or custom Spring events.
* Creating a Bean After Method Execution: Use an event listener or custom flags.
* Selective WebClient Interceptors: Use conditions, multiple WebClients, or ThreadLocal contexts.

***
***

### Using Conditional Bean Configuration in Spring Boot
Conditional bean configuration allows you to create and register beans conditionally based on certain properties or environment profiles. This is useful when you have multiple implementations or configurations, and you want to activate them depending on specific criteria.

Spring provides several annotations for conditional bean registration:

* @ConditionalOnProperty: Register a bean based on a specified property value.
* @ConditionalOnClass: Register a bean if a specific class is present in the classpath.
* @ConditionalOnMissingBean: Register a bean only if a specific bean is not already present.
* @Profile: Register a bean only when a certain Spring profile is active.
* @Conditional: A generic condition based on custom logic.
* @ConditionalOnBean: Register a bean only if another bean is already registered.
* @ConditionalOnMissingBean: Register a bean only if a specific bean is not present.

### Why Use Conditional Bean Configuration?
1. Flexibility: Easily switch between different implementations without changing the code.
2. Environment-Specific Beans: Use different configurations based on the environment or profile (e.g., development, production).
3. Simplifies Testing: Activate different beans during testing by changing the configuration.

***
***

Explanation of the Circular Dependency
In your configuration:

JwtFilter depends on UserDetailsService for extracting the user details (userDetailsService.loadUserByUsername(username)).

SecurityConfig provides a UserDetailsService bean (userDetailService in your case).

During the bean creation, SecurityConfig is trying to add jwtFilter as a filter in the securityFilterChain() method using .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class).

Since jwtFilter requires userDetailsService, which is provided by SecurityConfig, a circular dependency is formed:

JwtFilter → UserDetailsService → SecurityConfig → JwtFilter

1. Option 1: Use @Lazy to Break the Dependency
2. Option 2: Use Constructor Injection with Lazy Dependencies
3. Option 3: Separate the Beans into Different Configuration Classes






















































