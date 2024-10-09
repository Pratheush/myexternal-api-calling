package com.mylearning.journalapp.clientopenfeign;

import com.mylearning.journalapp.clientresponse.Person;
import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.gson.GsonDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/decorate-with feignbuilder-decoder")
@Slf4j
@ConditionalOnBean(FeignClientCodeBufferPersonClient.class)  //Register a bean only if another bean is already registered. Example: Combine with another condition
public class PersonFeignClientByNameAgeController {

    private final PersonFeignClientByNameAGe personClientByNameAge;

    //@Autowired
    public PersonFeignClientByNameAgeController(FeignClientCodeBufferPersonClient feignPersonClient) {
        this.personClientByNameAge = Feign.builder()
                .decoder(new GsonDecoder())
                .requestInterceptor(new RequestInterceptor(){
                    @Override
                    public void apply(RequestTemplate requestTemplate) {
                        String jwtAccessToken = feignPersonClient.getJwtAccessToken(); // Implement a method to fetch JWT token
                        if (jwtAccessToken != null && !jwtAccessToken.isEmpty()) {
                            log.info("FeignConfig RequestInterceptor if Block jwtAccessToken :: {}",jwtAccessToken);
                            requestTemplate.header("Authorization", jwtAccessToken);
                        }
                    }
                })
                .target(PersonFeignClientByNameAGe.class, "http://localhost:8081/api/person");
    }

        //this.feignPersonClient = feignPersonClient;
        @GetMapping(value = "/{name}/{age}")
    public Person getPersonByNameAndAgePathVariableExchange(@PathVariable String name, @PathVariable Integer age) {
        return personClientByNameAge.getPersonByNameAndAgePathVariableExchange(name,age);
    }
}




