package com.mylearning.journalapp.controller;

import com.mylearning.journalapp.clientresponse.Person;
import com.mylearning.journalapp.dto.UserDto;
import com.mylearning.journalapp.entity.User;
import com.mylearning.journalapp.service.PersonServiceWithRedis;
import com.mylearning.journalapp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
@Slf4j
public class PublicController {
    private final UserService userService;

    private final PersonServiceWithRedis personServiceWithRedis;

    @Autowired
    public PublicController(UserService userService, PersonServiceWithRedis personServiceWithRedis) {
        this.userService = userService;
        this.personServiceWithRedis = personServiceWithRedis;
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody UserDto userDto) {
        User user = User.builder()
                .userName(userDto.userName())
                .email(userDto.email())
                .password(userDto.password())
                .sentimentAnalysis(userDto.sentimentAnalysis())
                .build();
        String msg = userService.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(msg);
    }


    @GetMapping("/{firstName}/{age}")
    public ResponseEntity<Person> getPersonByNameAndAgePathVariable(@PathVariable String firstName, @PathVariable Integer age){
        Person personResponse = personServiceWithRedis.getPersonByNameAndAgePathVariable(firstName, age);
        return ResponseEntity.status(HttpStatus.OK).body(personResponse);
    }
}
