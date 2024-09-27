Problem: Transactions are not supported on a standalone MongoDB instance.
Solution: Convert the standalone MongoDB instance to a single-node replica set using --replSet rs0 in the docker-compose.yml.
Next Steps: Initialize the replica set by connecting to the MongoDB shell and running rs.initiate().

***
***
username fuwood90
password Frank@UnderWood@90


connection string:::
mongodb+srv://fuwood90:Frank@UnderWood@90@cluster0.daeur.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0


mongodb%2Bsrv%3A%2F%2Ffuwood90%3AFrank%40UnderWood%4090%40cluster0.daeur.mongodb.net%2F%3FretryWrites%3Dtrue%26w%3Dmajority%26appName%3DCluster0


@ %40
: %3A

mongodb+srv://fuwood90:Frank%40UnderWood%4090@cluster0.daeur.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0

***
***

To Enable Transaction Management user @Transactional on method level or class level
thus whole method which is doing modifications in the database that method will perform
all the transactional tasks as a whole as one unit i.e. as a single operation and in that unit if any tasks fails then rollback will happen thus all the modification will be rolled back if any exceptions occurs in any one task
and if all the tasks runs successfully then only 

use @EnableTransactionManagement which will do that find all the methods which are annotated with @Transactional annotation and springboot will create a transactional context
for those methods means a container in which the method related all the DB operations are there treated as single operation thus we are achieving Atomicity, 

Atomicity means :: if all the operations are successful then only considered its successful and if any task or operation fails then all the modifications done by the tasks or operations will be rolled back
Isolation is also achieved if two requests or two users are performing some operation on the same method here springboot will create two transactional contexts corresponding to the two requests hence two requests and their operations will be isolated from each other in their transactional contexts like a two containers containing two different requests performing operations




also configure TransactionManager which will handle all the transactions thus achieving ACID property 
PlatformTransactionManager is interface which is implemented by MongoTransactionManager 
so we create bean of PlatformTransactionManager which will return MongoTransactionManager which uses MongoDatabaseFactory 

MongoDatabaseFactory helps us to achieve connection to the Database
MongoDatabaseFactory is interface which is implemented by SimpleMongoClientDatabaseFactory which extends MongoDatabaseFactorySupport


***
***

### 1xx (Informational)
These status codes indicate that the request was received and understood and the server is continuing to process it. These are typically used for informational purposes and rarely seen in practice.

### 2xx (Successful)
These status codes indicate that the request was successfully received, understood, and processed by the serve.

like ::::
200 OK : The request has been successfully processed, and the server is returning the requested resource.
201 Created : The request has been fulfilled, and a new resource has been created as a result.
204 No Content : The request was successful, but there is no response body (typically used for operations that don't return data, like a successful deletion)


### 3xx (Redirection):
These status codes indicate that further action is needed to complete the request. They are used when the client needs to take additional steps to access the requested resource.

Like :::
301 Moved Permanently: The requested resource has been permanently moved to a different URL.
302 Found : The HTTP status code 302 indicated that the requested resource has been temporarily moved to different URL. When a server sends a response with a 302 status code. it typically includes a Location header field that specifies the new temporary URL where the client should redirect to.
304 Not Modified: The client's cached version of the requested resource is still valid, so the server sends this status code to indicate that the client can use its cached copy.

### 4xx (Client Error)::
These status codes indicate that there was an error on the client's part, such as malformed request or authentication issues.

400 Bad Request: The server cannot understand or process the client's request due to invalid syntax or other client-side issues.
401 Unauthorized: The client needs to provide authentication credentials to access the requested resource.
403 Forbidden : The client is authenticated, but it does not have permission to access the requested resource.

### 5xx (Server Error)
These status codes indicate that there was  an error on the server's part while trying to fulfill the request.

Like::::
500 Internal Server Error: A generic error message indicating that something went wrong on the server, and the server could not handle the request.
502 Bad Gateway: The server acting as a gateway or proxy received an invalid response from an upstream server.
503 Service Unavailable : The server is currently unable to handle the request due to temporary overloading or maintenance.

The ResponseEntity class is part of the Spring Framework and is commonly used in SpringBoot applications to customize the HTTP response. It provides methods for setting the response status, headers, and body. You can use it to return different types of data in your controller methods, such as JSON, XML, or even HTML.
We can use generics with ResponseEntity to specify the type of data you are returning.

***
***

### Authentication ::
The process of verifying a user's identity (username and password)

### Authorization ::
The Process of granting or denying access to specific resources or actions based on the authenticated user's roles and permissions.

By Default Spring Security uses HTTP Basic Authentication

<dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

<dependency>
      <groupId>org.springframework.security</groupId>
      <artifactId>spring-security-test</artifactId>
      <scope>test</scope>
    </dependency>


The Client Sends an Authorization Header. Authorization: Basic <encoded-string> The server decodes the string, extracts the username and password, and verifies them. if they're correct, access is granted. Otherwise, an "Unauthorized" response is sent back.

Encoding Credentials are combined into a string like username:password which is then encoded using Base64

By Default, all endpoints will be secured. Spring Security will generate a default user with a random password that is printed in the console logs on startup.

you can also configure username & password in application.properties

spring.security.user.name=user
spring.security.user.password=password


Customize Authentication :
'''
`@Configuration
@EnableWebSecurity
public class SpringSecurity extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
            http
		.authorizeRequests()
                .antMatchers("/journal/**", "/user/**").authenticated()		// request with /journal and /user need to be authenticated
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll();					// permit all request other than above without authentication
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().csrf().disable();
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

}`
'''
@EnableWebSecurity : This annotation signals Spring to enable its web security support. This is what makes your application secured. It's used in conjunction with @Configuration . @EnableWebSecurity this annotation tells we are going to customize spring security

WebSecurityConfigurerAdapter is a utility class in the Spring Security framework that provides default configurations and allows customization of certain features. By extending it, you can configure and customize Spring Security for your application needs.


This Configure method in Security provides way to configure how requests are secured. It defines how request matching should be done and what security actions should be applied.

http.authorizeRequests(): This tells Spring Security to start authorizing the requests.

.antMatchers("/hello").permitAll(): This part specifies that HTTP requests matching the path /hello should be permitted (allowed) for all users, whether they are authenticated or not.

.anyRequest().authenticated(): This is more general matcher that specifies any request (not already matched by previous matchers) should be authenticated , meaning users have to provide valid credentials to access these endpoints.

.and(): This is a method to join several configurations. It helps to continue the configuration from the root (HtttpSecurity).

.formLogin(): THis enables form-based authentication. By default, it will provide a form for the user to enter their username and password. if the user is not authenticated and they try to access a secured endpoint, they'll be redirected to the default login form.

'''
`@Configuration
@EnableWebSecurity
public class SpringSecurity extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
            http
		.authorizeRequests()
                    .antMatchers("/hello**").permitAll()		// request with /journal and /user need to be authenticated
                    .anyRequest().authenticated()
	        .and()
		formLogin();					// permit all request other than above without authentication
            }

}`
'''
You can access /hello without any authentication. However, if you try to access another endpoint, you'll be redirected to a login form

When we use the .formLogin() method in our security configuration without specifying .loginPage("/custom-path"), the default login page becomes active.

Spring Security provides an in-built controller that handles the /login path. This controller is responsible for rendering the default login form when a GET request is made to /login.

By default, Spring Security also provides logout functionality. When .logout() is configured, a POST request to /logout will log the user out and invalidate their session.

Basic Authentication, by its design, is stateless.
This means we are sending encoded username and password in Header. and we are sending to server and we get response. now when i send request second time we have to send encoded username and password again in header that means if i send 100 requests then i have to send encoded username and password in header 100 times for authentication
so stateless means second request doesn't know about what is sent in first request


Some applications do mix Basic Authentication with session management for various reasons. This isn't standard behaviour and requires additional setup and logic. In Such scenarios, once the user's credentials are verified via Basic Authentication, a session might be established, and the client is provided a session cookie. This way, the client won't need to send the Authorization header with every request, and the server can rely on the session cookie to identify the authenticated user.


When you login with Spring Security, it manages your authentication across multiple requests, despite HTTP being stateless.(HTTP Basic Authentication is stateless)
1. Session Creation : After successful authentication, an HTTP session is formed. Your authentication details are stored in this session.
2. Session Cookie : A JSESSIONID cookie is sent to your browser, which gets sent back with subsequent requests, helping the server recognize your session.
3. SecurityContext : Using the JSESSIONID, Spring Security fetches your authentication details for each request.
4. Session Timeout : Sessions have a limited life. if you're inactive past this limit, you're logged out.
5. Logout : When logging out, your session ends, and the related cookie is removed.
6. Remember-Me : Spring Security can remember you even after the session ends using a different persistent cookie(typically have a longer lifespan).

in essence, Spring Security leverages sessions and cookies, mainly JSESSIONID, to ensure you remain authenticated across requests.


We Want Our Spring Boot Application to authenticate users based on their credentials stored in a MongoDB database. This means that our users and their passwords (hashed) will be stored in the MongoDB, and when a user tries to login, the system should check the provided credentials against what's stored in the database.

A User entity to represent the user data model
A repository UserRepository to interact with MongoDB
UserDetailsService implementation to fetch user details
A configuration SecurityConfig to integrate everything with Spring Security


***
***

### Logging

Spring Boot Comes with a default logging configuration that uses Logback as the default logging implementation. It provides a good balance between simplicity and flexibility.

The Default configuration is embedded within the Spring Boot libraries, and it may not be visible in your Projects source code.

if you want to customize the logging configuration, you can create your own logback-spring.xml or logback.xml file in the src/main/resources directory.
When Spring Boot detects this file in your project, it will use it instead of the default configuration.

Logging levels help in categorizing log statements based on their severity. The common logging levels are
TRACE, DEBUG, INFO, WARN, ERROR

We can set the desired logging level for specific packages or classes, allowing them to control the amount of information logged at runtime.

By Default, logging is enabled for INFO, WARN, ERROR

Spring Boot provides annotations like @Slf4j & @Log4j2 that you can use to automatically inject logger instances into your classes.

Simple logging facade 4 java

Slf4j is abstraction above logback. we want to use logback and we use logback through Slf4j. Slf4j is logging abstraction framework. through Sl4j we can talk to underlying implementation which is here is logback. facade means abstraction.

Spring Boot allows us to configure logging using properties or YAML files.

logging.level.com.mylearning.journalApp=DEBUG


here we are talking about all the internal packaging so here we no need to mention about any specific libraries or packages specifically to record logs based on the severity just specifying root is enough to specify all the packages entirely.
logging.level.root=ERROR



logging.level.root=OFF
to turn off all the logs completely. here specifying root means we are specifying all the packages our software package and all the libraries internally will be stopped all at once with root and when we specify OFF will turn off the logs.

to stop logging of any specific package ::
logging.level.com.mylearning.journalapp.services=OFF

to stop logging for any specific class:::
logging.level.com.mylearning.journalapp.services.UserService=OFF


logback.xml ::::::::::::::::::

```
<configuration>

    <appender name = "myConsoleAppender" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>
                %d{yy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg %n
            </pattern>
        </encoder>
    </appender>

    <appender name = "myFileAppender" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>
            journalApp.log
        </file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>journalApp-%d{yy-MM-dd_HH-mm}.%i.log</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>10</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>
                %d{yy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg %n
            </pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="myConsoleAppender" />
        <appender-ref ref="myFileAppender" />
    </root>


</configuration>


```
<configuration>
	<!-- Appender and Logger Configuration go here -->
</configuration>


The <Configuration> element is the root element of the logback.xml file. All Logback configuration is enclosed within this element.

Appenders : appender tells where to print logs
Console Appender
File Appender

Encoder : encoder tells in which format we want to print logs when we use encoder we also have to specify pattern element.
when we use File Appender we have to specify file element with the file name and location

class="ch.qos.logback.core.rolling.RollingFileAppender"
we can also use class="ch.qos.logback.core.FileAppender" but this cannot do rolling
rolling means : FileAppender does not do rotation meaning that it is not creating new log file whenever file size limit is exceeding beyond the specified size.
or whenever the specified file-name pattern is changing like here >>>>  %d{yy-MM-dd_HH-mm}.%i.log here _HH-mm means every hour or every minute new file gets created
so RollingFileAppender creates new file based on time based rotation or file size limit rotation this is we call as Rolling of Logs.

%d{yy-MM-dd}.%i.log if fileNamePattern is specified like this then daily roll-over will happen thus creating new logs daily

%d{yy-MM-dd_HH}.%i.log if fileNamePattern is specified like this then every hour roll-over will happen thus creating new logs every hour

%d{yy-MM-dd_HH-mm}.%i.log if fileNamePattern is specified like this then every minute roll-over will happen thus creating new logs every minute

%d{yy-MM-dd_HH-mm}.%i.log here %i means index

keep the file history upto 10 and delete the oldest files

***
***

### Sonar Qube 

docker run --name sonarqube-custom -p 9000:9000 sonarqube:10.6-community
docker run -d --name sonarqube-cust -p 9000:9000 sonarqube:lts

in pom.xml ::: in plugin:::

<!-- SonarQube plugin -->
            <plugin>
                <groupId>org.sonarsource.scanner.maven</groupId>
                <artifactId>sonar-maven-plugin</artifactId>
                <version>4.0.0.4121</version>
            </plugin>

mvnw clean install sonar:sonar -Dsonar.login=sqa_44c9d2770320b1354678965839f183087127af9b

for sonar scanning ::
mvn sonar:sonar -Dsonar.login=sqa_44c9d2770320b1354678965839f183087127af9b


sqa_44c9d2770320b1354678965839f183087127af9b

mvn sonar:sonar -Dsonar.login=your_generated_token

***
***

























