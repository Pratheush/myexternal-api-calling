package com.mylearning.journalapp.controller;

import com.mylearning.journalapp.dto.UserDto;
import com.mylearning.journalapp.entity.User;
import com.mylearning.journalapp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/user")
//@Profile("atlas")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getUserByUsername() {
        log.info(" UserController getUserByUsername called");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String userName = authentication.getName();
        User user = userService.getUserByUsername(userName);
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .password("******")
                .email(user.getEmail())
                .roles(user.getRoles())
                .journalEntries(user.getJournalEntries())
                .build();
        log.info("UserController getUserByUsername UserDto :: userName : {}, password : {}, email : {}",userDto.userName(),user.getPassword(),userDto.email());
        return ResponseEntity.status(HttpStatus.OK).body(userDto);
    }

    @PutMapping
    public ResponseEntity<?> updateUserByUsername(@RequestBody UserDto userDto) {

        User user = User.builder()
                .userName(userDto.userName())
                .email(userDto.email())
                .password(userDto.password())
                .build();

        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String userName = authentication.getName();
        String msg = userService.updateUserByUsername(user,userName);
        return ResponseEntity.status(HttpStatus.OK).body(msg);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUserByUsername() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String userName = authentication.getName();
        String msg = userService.deleteUserByUsername(userName);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(msg);
    }
}
