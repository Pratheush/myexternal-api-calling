package com.mylearning.journalapp.service;

import com.mylearning.journalapp.entity.User;
import com.mylearning.journalapp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/***
 *
 * Fallback to Another Data Source:
 * If you want to fetch the user from another data source (e.g., another database, cache, or external API)
 * when the user is not found in the primary UserRepository, you can add an additional lookup method.
 *
 * '''
 *
 * @Service
 * @Slf4j
 * public class UserDetailServiceImpl implements UserDetailsService {
 *
 *     private final UserRepository userRepository;
 *     private final SecondaryUserRepository secondaryUserRepository; // Optional fallback source
 *
 *     public UserDetailServiceImpl(UserRepository userRepository, SecondaryUserRepository secondaryUserRepository) {
 *         log.info("UserDetailServiceImpl constructor called.");
 *         this.userRepository = userRepository;
 *         this.secondaryUserRepository = secondaryUserRepository;
 *     }
 *
 *     @Override
 *     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
 *         log.info("UserDetailServiceImpl loadUserByUsername() called for username :: {}", username);
 *
 *         return userRepository.findByUserName(username)
 *                 .map(userFrmDB -> buildUserDetails(userFrmDB))
 *                 .orElseGet(() -> fallbackToSecondarySource(username));
 *     }
 *
 *     private UserDetails buildUserDetails(UserEntity userFrmDB) {
 *         String[] rolesArray = userFrmDB.getRoles().toArray(String[]::new);
 *         log.info("Roles for username {}: {}", userFrmDB.getUserName(), rolesArray);
 *
 *         return User.builder()
 *                 .username(userFrmDB.getUserName())
 *                 .password(userFrmDB.getPassword())
 *                 .roles(rolesArray)
 *                 .build();
 *     }
 *
 *     private UserDetails fallbackToSecondarySource(String username) {
 *         log.info("User not found in primary source, checking fallback source for username :: {}", username);
 *
 *         return secondaryUserRepository.findByUserName(username)
 *                 .map(userFrmDB -> buildUserDetails(userFrmDB))
 *                 .orElseThrow(() -> new UsernameNotFoundException(String.format("User Not Found with Username: %s in both primary and fallback source", username)));
 *     }
 * }
 *
 * '''
 *
 * Primary Lookup: The system first tries to fetch the user from the primary userRepository
 * Fallback Lookup: If the user is not found, it falls back to secondaryUserRepository (which could be another database, cache, or external API).
 * If User Not Found in Either Source: If neither the primary nor the secondary data source contains the user, it throws a UsernameNotFoundException
 *
 * Fallback to a Default User:
 * If you want to provide a default user as a fallback when the requested user is not found,
 * you can create a predefined UserDetails object to be returned.
 *
 * Default User Fallback: If the requested user is not found,
 * a default user is returned (with username "defaultUser" and a default password and role).
 *
 * Logging: You can add a warning log to track when the fallback is being used.
 */
//@Service
@Slf4j
public class UserDetailsServiceImplWithFallBackMethod implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImplWithFallBackMethod(UserRepository userRepository) {
        log.info("UserDetailServiceImpl constructor called.");
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("UserDetailServiceImpl loadUserByUsername() called for username :: {}", username);

        // inside map() ::: userFrmDB -> buildUserDetails(userFrmDB)
        return userRepository.findByUserName(username)
                .map(this::buildUserDetails)
                .orElseGet(() -> fallbackToDefaultUser(username));
    }

    private UserDetails buildUserDetails(User userFrmDB) {
        //String[] rolesArray = userFrmDB.getRoles().toArray(String[]::new);

        // Check if roles are null or empty and handle accordingly
        List<String> rolesList = userFrmDB.getRoles();
        String[] rolesArray = (rolesList != null && !rolesList.isEmpty())  // checks if the roles list is not null and not empty
                ? rolesList.toArray(new String[0])
                : new String[] {}; // Default to an empty array if roles are null or empty. If the roles list is null or empty, the rolesArray is set to an empty array (new String[] {}).

        log.info("Roles for username {}: {}", userFrmDB.getUserName(), rolesArray);

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(userFrmDB.getUserName())
                .password(userFrmDB.getPassword())
                .roles(rolesArray)
                .build();
    }

    private UserDetails fallbackToDefaultUser(String username) {
        log.warn("User not found for username: {}, returning default user", username);

        return org.springframework.security.core.userdetails.User
                .builder()
                .username("defaultUser")
                .password("{noop}defaultPass")  // Use noop for plain text password
                .roles("ROLE_USER")  // Default role
                .build();
    }
}
