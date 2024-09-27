package com.mylearning.journalapp.service;

import com.mylearning.journalapp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Suggested Improvements:
 * AtomicReference Usage:
 * While the use of AtomicReference works here, it's generally not necessary unless you're dealing with concurrency.
 * Since this is a simple assignment within a lambda, you can just use a regular variable.
 *
 * Exception Message:
 * There is a small typo in the UsernameNotFoundException. You are using STR. which should not be there. Also,
 * using a proper message format (like String.format or logging directly) is a good idea.
 *
 * Logging Best Practices:
 * You can make the log statements more meaningful and avoid unnecessary logs.
 * Ensure logging does not expose sensitive information like passwords, even though this example does not include it.
 *
 * Roles Conversion:
 * The use of toArray(String[]::new) for roles is fine, but I suggest adding null/empty checks to make it more robust.
 *
 * Thread Safety:
 * Consider thread safety if you expect concurrent access to the loadUserByUsername method, although the current structure does not suggest concurrency issues.
 *
 * @Override
 *     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
 *         log.info("UserDetailServiceImpl loadUserByUsername() called for username :: {}", username);
 *
 *         return userRepository.findByUserName(username)
 *                 .map(userFrmDB -> {
 *                     String[] rolesArray = userFrmDB.getRoles().toArray(String[]::new);
 *                     log.info("UserDetailServiceImpl roles for username {}: {}", username, rolesArray);
 *
 *                     return User.builder()
 *                             .username(userFrmDB.getUserName())
 *                             .password(userFrmDB.getPassword())
 *                             .roles(rolesArray)
 *                             .build();
 *                 })
 *                 .orElseThrow(() -> new UsernameNotFoundException(String.format("User Not Found with Username: %s", username)));
 *     }
 *
 *  Key Changes:
 * Removed AtomicReference:
 * Instead of using an AtomicReference, I directly return the mapped UserDetails.
 *
 * Exception Message:
 * Fixed the typo and used String.format() to create the exception message cleanly.
 *
 * Logging:
 * Logging of roles is safer now, without exposing sensitive user details.
 * Password logging is avoided by design in Spring Security, so be careful not to log sensitive information.
 *
 * Optional Improvements:
 * You might want to add additional exception handling or a fallback if needed.
 *
 */
@Service
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService{

    private final UserRepository userRepository;

    public UserDetailServiceImpl(UserRepository userRepository) {
        log.info("UserDetailServiceImpl constructor");
        this.userRepository = userRepository;
    }

    /**
     * toArray(new String[0])) it means roles is in list so convert into array and Using String we define what type of
     * array then 0 tells to resize the array. we created new array and if getRoles size is greater than 0 then resize the String Array accordingly
     * @param username the username identifying the user whose data is required.
     * @return UserDetails is required for Authentication and Authorization
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("UserDetailServiceImpl loadUserByUsername() called for username :: {}", username);

        return userRepository.findByUserName(username)
                .map(userFrmDB -> {
                    // Check if roles are null or empty and handle accordingly
                    List<String> rolesList = userFrmDB.getRoles();
                    String[] rolesArray = (rolesList != null && !rolesList.isEmpty())  // checks if the roles list is not null and not empty
                            ? rolesList.toArray(new String[0])
                            : new String[] {}; // Default to an empty array if roles are null or empty. If the roles list is null or empty, the rolesArray is set to an empty array (new String[] {}).

                    log.info("UserDetailServiceImpl roles for username {}: {}", username, rolesArray);

                    return User.builder()
                            .username(userFrmDB.getUserName())
                            .password(userFrmDB.getPassword())
                            .roles(rolesArray)
                            .build();
                })
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User Not Found with Username: %s", username)));

        /*log.info("UserDetailServiceImpl loadUserByUsername() called username :: {}",username);
        AtomicReference<UserDetails> userDetails = new AtomicReference<>();
        userRepository.findByUserName(username).ifPresentOrElse(userFrmDB -> {
            userDetails.set(User.builder()
                    .username(userFrmDB.getUserName())
                    .password(userFrmDB.getPassword())
                    .roles(userFrmDB.getRoles().toArray(String[]::new))     // toArray(new String[0])) it means roles is in list so convert into array and Using String we define what type of array then 0 tells to resize the array. we created new array and if getRoles size is greater than 0 then resize the String Array accordingly
                    .build());
            log.info("UserDetailServiceImpl userDetails :: {}",userDetails.get());
        },() -> {
            throw new UsernameNotFoundException(STR."User Not Found with Username : \{username}");
                  });
        return userDetails.get();*/
    }
}
