package com.mylearning.journalapp.clientopenfeign;

import feign.Feign;
import feign.Logger;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.ErrorDecoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * Decoder – ResponseEntityDecoder, which wraps SpringDecoder, used to decode the Response
 * Encoder – SpringEncoder is used to encode the RequestBody.
 * Logger – Slf4jLogger is the default logger used by Feign.
 * Contract – SpringMvcContract, which provides annotation processing
 * Feign-Builder – HystrixFeign.Builder is used to construct the components.
 * Client – LoadBalancerFeignClient or default Feign client
 *
 */
@EnableFeignClients(basePackages="com.mylearning.journalapp")
@Configuration
public class FeignConfig {


}
