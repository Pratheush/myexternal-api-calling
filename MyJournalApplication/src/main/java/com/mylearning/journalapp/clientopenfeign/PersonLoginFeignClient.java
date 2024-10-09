package com.mylearning.journalapp.clientopenfeign;

import com.mylearning.journalapp.clientresponse.JWTAuthResponse;
import com.mylearning.journalapp.clientresponse.LoginDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "PERSON-LOGIN-FEIGN-CLIENT", url = "http://localhost:8081/api/codebuffer/person")
public interface PersonLoginFeignClient {

    @PostMapping("/login")
    JWTAuthResponse login(@RequestBody LoginDto loginDto);
}
