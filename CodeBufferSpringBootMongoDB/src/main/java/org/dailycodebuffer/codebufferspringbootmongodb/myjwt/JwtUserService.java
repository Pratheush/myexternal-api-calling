package org.dailycodebuffer.codebufferspringbootmongodb.myjwt;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class JwtUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailServiceImpl userDetailsService;

    public JwtUserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserDetailServiceImpl userDetailsService) {
        log.info("JwtUserService constructor called");
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Transactional
    public String addUser(User user) {
        log.info("JwtUserService addUser() called");
        // Check if the username already exists
        Optional<User> userAlreadyExists = userRepository.findByUserName(user.getUserName());
        if(userAlreadyExists.isPresent()) return String.format("User Already Exist : %s",user.getUserName());

        // Encode the password
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // If the user doesn’t provide any roles, the default role of "USER" is assigned.
        // Assign default roles if none provided
        List<Role> userRoles = user.getRoles();
        if(userRoles==null){
            log.info("JwtUserService addUser() if Block called userRoles is null");
            Role defaultRole  = roleRepository.findByName("USER").orElseGet(() -> roleRepository.save(new Role(new ObjectId(), "USER")));
            user.setRoles(List.of(defaultRole));
        }else{
            // If roles are provided, each role is checked against the database. If it doesn’t exist, it’s saved and assigned to the user.
            // Save any new roles provided with the user
            log.info("JwtUserService addUser() else Block called");
            List<Role> savedRoles = user.getRoles().stream()
                    .map(role ->
                         roleRepository.findByName(role.getName())
                                .orElseGet(() -> roleRepository.save(role))
                    ).toList();
            user.setRoles(savedRoles);
        }

        // Save the user with roles references
        userRepository.save(user);

        return String.format("User created : %s",user.getUserName());
    }

    /**
     * In the login() method, the authenticationManager attempts to authenticate the user by passing
     * their loginDto credentials to the UsernamePasswordAuthenticationToken.
     * If the authentication is successful, a token is generated using the jwtUtil object and returned to the caller.
     * @param loginDto
     * @return
     */
    public String login(LoginDto loginDto) {
        log.info("JwtUserService login() called");
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(), loginDto.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getUsernameOrEmail());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return jwtUtil.generateToken(userDetails.getUsername());
        }catch (Exception e){
            log.error("Exception occurred while createAuthenticationToken ", e);
            return String.format(" Authentication Failed. Incorrect Credentials : %s",loginDto.getUsernameOrEmail());
            //return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
        }
    }
}
