package com.mylearning.journalapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mylearning.journalapp.clientexception.PersonCallingClientException;
import com.mylearning.journalapp.exception.PersonJsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisService {
    private final RedisTemplate<String,String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public RedisService(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }
    
    public <T>T get(String key,Class<T> classType) {
        try {
            Object objectResponse = redisTemplate.opsForValue().get(key);
            if (objectResponse != null) return objectMapper.readValue(objectResponse.toString(), classType);
            else throw PersonJsonProcessingException.personException("Exception Occurred In Get RedisService Unable To GET READ VALUE From REDIS Key");
        } catch (JsonProcessingException | RuntimeException e) {
            log.error("RedisService get Exception Occurred : {}", e.getMessage());
            log.error("RedisService get going to return null");
            return null;
        }
    }

    public void set(String key, Object value, Long ttl) {
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key,jsonValue,ttl, TimeUnit.MILLISECONDS);
        } catch (JsonProcessingException | RuntimeException e) {
            log.error("RedisService set Exception Occurred  Unable to SET VALUE TO REDIS: {}", e.getMessage());

        }
    }
}
