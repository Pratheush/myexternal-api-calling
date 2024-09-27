package org.dailycodebuffer.codebufferspringbootmongodb.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@SpringBootTest(classes = PostConfig.class)
class PostServiceMockRestServiceServerUnitTest {

    /**
     * @ExtendWith: This annotation is used to register one or more extensions
     * {MockitoExtension.class, SpringExtension.class}:
     * This part specifies an array of classes that represent the extensions to be used.
     *
     *
     * MockitoExtension: This extension integrates Mockito with JUnit 5, allowing you to use Mockito annotations like
     * @Mock, @InjectMocks, etc., in your tests.
     *
     * SpringExtension: This extension integrates JUnit 5 with the Spring framework, providing support for
     * features such as dependency injection and other Spring-related functionality in your tests.
     *
     *@SpringBootTest(classes = PostConfig.class)
     * @SpringBootTest: This annotation is used to signal that the annotated class should be treated as a Spring Boot test.
     * It triggers the loading of the Spring ApplicationContext and provides a set of features for testing Spring Boot applications.
     *
     * The classes attribute is used to specify the configuration classes
     * that should be used to configure the Spring ApplicationContext and PostConfig.class has RestTemplate and PostService Configured as Bean
     * to available and managed by Spring ApplicationContext.
     *
     *
     * The Spring Test module includes a mock server named MockRestServiceServer.
     * With this approach, we configure the server to return a particular object
     * when a specific request is dispatched through our RestTemplate instance.
     * In addition, we can verify() on that server instance
     *
     * MockRestServiceServer actually works by intercepting the HTTP API calls using a MockClientHttpRequestFactory.
     * Based on our configuration, it creates a list of expected requests and corresponding responses.
     * When the RestTemplate instance calls the API, it looks up the request in its list of expectations,
     * and returns the corresponding response.
     *
     */


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private  PostService postService;

    private MockRestServiceServer mockServer;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void testGetAllPost() throws URISyntaxException, JsonProcessingException {

        Post post1=new Post(1,2,"Testing RestTemplate Using Mockito","Testing RestTemplate Using Mockito we defined the behavior of our mock using Mockito’s when/then ");
        Post post2=new Post(2,3,"Post 2","Post 2 about using Mockito on RestTemplate");
        List<Post> posts=List.of(post1,post2);
        String post2Url="https://jsonplaceholder.typicode.com/posts";

        mockServer.expect(ExpectedCount.times(1), MockRestRequestMatchers.requestTo(new URI(post2Url)))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(posts)));

        List<Post> allPosts = postService.getAllPosts();

        Assertions.assertNotNull(allPosts);
        /*org.assertj.core.api.Assertions.assertThat(allPosts).hasSize(2);
        org.assertj.core.api.Assertions.assertThat(allPosts).containsExactly(post1,post2);*/

        // JOINED TWO ASSERTIONS
        org.assertj.core.api.Assertions.assertThat(allPosts).hasSize(2).containsExactly(post1,post2);

        mockServer.verify();


    }
}
