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






























































































































































