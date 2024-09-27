package org.dailycodebuffer.codebufferspringbootmongodb.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class PostService {

    private static final Log log = LogFactory.getLog(PostService.class);

    private final RestTemplate restTemplate;

    public PostService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /*public PostService(RestTemplateBuilder builder) {
        this.restTemplate = builder
        .setConnectTimeout(Duration.ofMillis(3000))
        .setReadTimeout(Duration.ofMillis(3000))
        .build();
    }*/

    public List<Post> getAllPosts(){
        String url="https://jsonplaceholder.typicode.com/posts";
        //My Mistake
       /*ResponseEntity<Post[]> resp1= restTemplate.getForEntity(url, Post[].class);
       List<Post> postList= Arrays.asList(resp1.getBody());*/
        ResponseEntity<List<Post>> resp=restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Post>>() {});
       return resp.getStatusCode() == HttpStatus.OK ? resp.getBody() : null;
    }

    public  Post getPostById(Integer id){
        ResponseEntity<Post> resp= restTemplate.getForEntity("https://jsonplaceholder.typicode.com/posts/", Post.class,id);
        return resp.getStatusCode() == HttpStatus.OK ? resp.getBody() : null;
    }



}
