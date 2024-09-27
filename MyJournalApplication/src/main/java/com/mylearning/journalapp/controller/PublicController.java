package com.mylearning.journalapp.controller;

import com.mylearning.journalapp.dto.UserDto;
import com.mylearning.journalapp.entity.User;
import com.mylearning.journalapp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
@Slf4j
public class PublicController {
    private final UserService userService;

    public PublicController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody UserDto userDto) {
        User user = User.builder()
                .userName(userDto.userName())
                .email(userDto.email())
                .password(userDto.password())
                .build();
        String msg = userService.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(msg);
    }


}
