### Setting Up Feign Client with Spring Boot
```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>

```

We would also need to add the spring-cloud-dependencies since we need Spring Cloud in our project.
```
<dependencyManagement>
	<dependencies>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-dependencies</artifactId>
			<version>2022.0.3</version>
			<type>pom</type>
			<scope>import</scope>
		</dependency>
	</dependencies>
</dependencyManagement>
```

### Enabling Feigh Clients with @EnableFeignClients
enable Feign Clients using @EnableFeignClients annotation that enables component scanning for all interfaces annotated with @FeignClient.
```
@EnableFeignClients(basePackages="com.howtodoinjava.feign.client")
@Configuration
public class FeignConfig {
  //...
}
```

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




























































































































































