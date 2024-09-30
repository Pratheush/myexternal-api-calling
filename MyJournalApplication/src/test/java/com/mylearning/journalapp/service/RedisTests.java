package com.mylearning.journalapp.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class RedisTests {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Test
    void testRedisServerSetup(){
        redisTemplate.opsForValue().set("email", "gmail@email.com");
        String emailGet = redisTemplate.opsForValue().get("email");
        Assertions.assertEquals("gmail@email.com",emailGet);

        // before asserting below statement first in redis-cli >>  set salary 100000 then assert by getting the value
        Object sal = redisTemplate.opsForValue().get("salary");
        Assertions.assertEquals("10k",sal);
    }
}
