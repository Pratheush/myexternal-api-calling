package org.dailycodebuffer.codebufferspringbootmongodb.client;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private RestTemplate restTemplate=new RestTemplate();

    @InjectMocks
    private PostService postService;

    // Using Mockito :::: ways of mocking calls performed only through a RestTemplate.
    @Test
    void testGetPostById(){

        Post post1=new Post(1,2,"Testing RestTemplate Using Mockito","Testing RestTemplate Using Mockito we defined the behavior of our mock using Mockito’s when/then ");
        String post2Url="https://jsonplaceholder.typicode.com/posts/";
        Mockito.when(restTemplate.getForEntity(post2Url,Post.class,2))
                .thenReturn(new ResponseEntity<>(post1, HttpStatus.OK));


        Post postResult = postService.getPostById(2);

        Assertions.assertNotNull(postResult);
        org.assertj.core.api.Assertions.assertThat(postResult).isEqualTo(post1);

    }

    @Test
    void testGetAllPosts(){

        Post post1=new Post(1,2,"Testing RestTemplate Using Mockito","Testing RestTemplate Using Mockito we defined the behavior of our mock using Mockito’s when/then ");
        Post post2=new Post(2,3,"Post 2","Post 2 about using Mockito on RestTemplate");
        List<Post> posts=List.of(post1,post2);
        String post2Url="https://jsonplaceholder.typicode.com/posts";

        Mockito.when(restTemplate.exchange(post2Url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Post>>() {}))
                .thenReturn(new ResponseEntity<List<Post>>(posts, HttpStatus.OK));


        List<Post> postResult = postService.getAllPosts();

        Assertions.assertNotNull(postResult);
        org.assertj.core.api.Assertions.assertThat(postResult).containsExactly(post1,post2);

    }
}
