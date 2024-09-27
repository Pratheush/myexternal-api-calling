package com.mylearning.journalapp.service;

import com.mylearning.journalapp.entity.User;
import com.mylearning.journalapp.exception.UserNotFoundException;
import com.mylearning.journalapp.repository.JournalEntryRepository;
import com.mylearning.journalapp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
//@Profile("atlas")
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JournalEntryRepository journalEntryRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JournalEntryRepository journalEntryRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.journalEntryRepository = journalEntryRepository;
    }

    public Optional<User> findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Transactional
    public String addUser(User user) {
        Optional<User> optUserName = userRepository.findByUserName(user.getUserName());
        if(optUserName.isEmpty()) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            user.setRoles(Arrays.asList("USER"));
            userRepository.save(user);
            return String.format("User created : %s",user.getUserName());
        }else {
            return String.format("User Already Exist : %s",user.getUserName());
        }
    }

    public User getUserByUsername(String userName) {
        log.info("UserService getUserByUsername called");
        User userFrmDB = userRepository.findByUserName(userName).orElseThrow(() -> new UserNotFoundException(String.format("User not found with Username : %s", userName)));
        log.info("UserService getUserByUsername UserDto :: userName : {}, password : {}, email : {}",userFrmDB.getUserName(),userFrmDB.getPassword(),userFrmDB.getEmail());
        return userFrmDB;
    }

    @Transactional
    public String updateUserByUsername(User user, String userName) {
        // since user already provided username and password, so it means user exists so no need to check and throw exception again
        /*User savedUser = userRepository.findByUserName(userName).orElseThrow(() -> new UserNotFoundException(STR."User not found with Username :\{userName}"));
        savedUser.setEmail(!user.getEmail().isEmpty() && !user.getEmail().isBlank() ? user.getEmail() : savedUser.getEmail());
        savedUser.setPassword(!user.getPassword().isEmpty() && !user.getPassword().isBlank() ? passwordEncoder.encode(user.getPassword()) : savedUser.getPassword());
        return userRepository.save(savedUser);*/

        userRepository.findByUserName(userName).ifPresent(userFrmDb -> {
            userFrmDb.setEmail(!user.getEmail().isEmpty() && !user.getEmail().isBlank() ? user.getEmail() : userFrmDb.getEmail());
            userFrmDb.setPassword(!user.getPassword().isEmpty() && !user.getPassword().isBlank() ? passwordEncoder.encode(user.getPassword()) : userFrmDb.getPassword());
            userRepository.save(userFrmDb);
        });
        return "User Updated";
    }

    @Transactional
    public String deleteUserByUsername(String userName) {
        userRepository.findByUserName(userName).ifPresentOrElse(user -> {
            userRepository.deleteByUserName(user.getUserName());
            if(!user.getJournalEntries().isEmpty()) journalEntryRepository.deleteAll(user.getJournalEntries());
        },() ->{
            throw new UserNotFoundException(String.format("USER NOT FOUND WITH USERNAME : %s",userRepository));
        });
        return String.format("User Deleted with Username: %s",userName);
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public String saveAdmin(User user) {
        Optional<User> optUserName = userRepository.findByUserName(user.getUserName());
        if(optUserName.isEmpty()) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            user.setRoles(List.of("ADMIN"));
            userRepository.save(user);
            return String.format("User Created With Username : %s",user);
        }else {
            return String.format("User Already Exist : %s",user.getUserName());
        }
    }
}
