package com.mylearning.journalapp.controller;

import com.mylearning.journalapp.dto.UserDto;
import com.mylearning.journalapp.entity.User;
import com.mylearning.journalapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all-users")
    public ResponseEntity<?> getAllUsers(){
        List<User> allUsers = userService.getAllUsers();

        List<UserDto> userDtos = allUsers.stream().map(user -> UserDto.builder()
                        .id(user.getId())
                        .userName(user.getUserName())
                        .email(user.getEmail())
                        .journalEntries(user.getJournalEntries())
                        .roles(user.getRoles())
                        .build())
                .toList();

        if(!userDtos.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(userDtos);
        }
        return new ResponseEntity<>("No Users Found",HttpStatus.NOT_FOUND);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> addAdmin(@RequestBody UserDto userDto) {
        User user = User.builder()
                .userName(userDto.userName())
                .email(userDto.email())
                .password(userDto.password())
                .build();
        String msg = userService.saveAdmin(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(msg);
    }
}
