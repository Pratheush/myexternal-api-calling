package com.mylearning.journalapp.clientopenfeign;


import org.bson.Document;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(value = "PERSON-FEIGN-CLIENT-REQUEST-HEADER", url = "http://localhost:8081/api/person")
public interface PersonFeignClientUsingRequestHeader {
    @GetMapping(value = "/populationByCity")
    ResponseEntity<List<Document>> getPopulationByCity(@RequestHeader("Authorization") String jwtToken);
}
