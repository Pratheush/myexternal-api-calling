package org.dailycodebuffer.codebufferspringbootmongodb.myjwt;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Deprecated features include:
 *
 * WebSecurityConfigAdapter
 * EnableGlobalMethodSecurity
 * antMatcher() and csrf()
 * authorizeRequest()
 *
 * ----------------------------------------------------------------
 * In Spring Security version 5.7.0-M2, WebSecurityConfigurerAdapter class had been deprecated to encourage
 * the component-based security approach to configure HttpSecurity by creating a SecurityFilterChain bean.
 *
 * @Configuration
 * @EnableWebSecurity
 * public class SecurityConfig extends WebSecurityConfigurerAdapter {
 *
 * @Override
 *     protected void configure(HttpSecurity http) throws Exception {
 *        http.authorizeRequests()
 *                .antMatchers("/public/**")
 *                     .permitAll()
 *                .anyRequest()
 *                     .authenticated()
 *                .and()
 *                 .formLogin();
 *     }
 * }
 *----------------------------------------------------------------
 * In Spring Security 6, the same configuration
 * @Configuration
 * @EnableWebSecurity
 * public class SecurityConfig {
 *
 *     @Bean
 *     public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
 *
 *         httpSecurity.authorizeHttpRequests()
 *                 .requestMatchers("/public/**").permitAll()
 *                 .anyRequest().authenticated()
 *                 .and()
 *                 .formLogin();
 *
 *         return  httpSecurity.build();
 *     }
 * }
 *=======================================================================================================================
 *
 * Replace antMatchers, mvcMatchers, regexMatchers & authorizeRequests with requestMatchers and authorizeHttpRequests
 * the authorizeRequests method has been deprecated and we should use the authorizeHttpRequests method instead.
 *
 * For example, the following configuration is written with WebSecurityConfigurerAdapter:
 *
 * http.authorizeRequests()
 *   .antMatchers("/h2-console/**").permitAll()
 *   .anyRequest().authenticated();
 *
 * With Spring Boot 3 Security
 *http.authorizeHttpRequests()
 *   .requestMatchers("/h2-console/**").permitAll()
 *   .anyRequest().authenticated();
 *
 *=======================================================================================================================
 *
 * @EnableGlobalMethodSecurity is Deprecated
 * The @EnableGlobalMethodSecurity was used to enable method-level security across the entire application using the annotations such as @Secured, @PreAuthorize, @PostAuthorize etc.
 * In Spring Security 6, @EnableGlobalMethodSecurity has been deprecated in favor of @EnableMethodSecurity.
 * @EnableMethodSecurity enables the pre and post annotations, by default.
 * This means that we no longer need to set ‘prePostEnabled = true‘ explicitly.
 *
 * Now we can replace the following declaration:
 * @Configuration
 * @EnableWebSecurity
 * @EnableGlobalMethodSecurity(prePostEnabled = true)
 * public class SecurityConfig {
 *   //...
 * }
 *
 * with the new declaration as follows:
 * @Configuration
 * @EnableWebSecurity
 * @EnableMethodSecurity
 * public class SecurityConfig {
 *   //...
 * }
 *
 * =========================================================================================================================
 *
 * Default Authorities foroauth2Login()
 *
 * In earlier versions of Spring Security, the default authority given to a user who authenticates via oauth2Login() for both OAuth2 and OIDC providers was ROLE_USER.
 *
 * However, in Spring Security 6, the default authority given to a user who authenticates with an OAuth2 provider is OAUTH2_USER, while the default authority given to a user who authenticates with an OpenID Connect 1.0 provider is OIDC_USER.
 *
 *
 * Changes to Common Configurations::::::::::::::
 *
 * inMemoryAuthentication() to InMemoryUserDetailsManager
 *
 * UserDetailsManager is a sub-interface of UserDetailsService that extends it with additional methods for managing user accounts.
 * InMemoryUserDetailsManager is an implementation of the UserDetailsManager interface that is used to store user details in memory, typically used for unit testing and POC purposes.
 *
 * In the following example, we use WebSecurityConfigurerAdapter to configure the AuthenticationManager to use in-memory authentication.
 * @Configuration
 * @EnableWebSecurity
 * public class SecurityConfig extends WebSecurityConfigurerAdapter {
 *
 * 	    @Override
 *    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
 *
 * 		UserDetails admin = User.withDefaultPasswordEncoder()
 * 			.username("admin")
 * 			.password("1234")
 * 			.roles("ADMIN")
 * 			.build();
 *
 * 		auth.inMemoryAuthentication().withUser(admin);
 *    }
 * }
 *
 *
 * In Spring Security 6, we can define a bean of type InMemoryUserDetailsManager and provide the user details.
 * @Configuration
 * @EnableWebSecurity
 * public class SecurityConfig {
 *
 * 	    @Bean
 *    public InMemoryUserDetailsManager inMemoryUserDetailsManager(){
 *
 * 		UserDetails admin = User.withDefaultPasswordEncoder()
 * 			.username("admin")
 * 			.password("1234")
 * 			.roles("ADMIN")
 * 			.build();
 *
 * 		return new InMemoryUserDetailsManager(admin);
 *    }
 * }
 * -----------------------------------------------------------------------------------------------
 *
 * jdbcAuthentication() to JdbcUserDetailsManager
 * Another implementation of the UserDetailsManager interface is JdbcUserDetailsManager. It stores user information in a relational database using JDBC.
 *
 * JDBC authentication using WebSecurityConfigurerAdapter by overriding the configure() method. Spring uses the DataSource bean for connecting to the database.
 *
 * @Configuration
 * @EnableWebSecurity
 * public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
 *
 * 	    @Autowired
 *    private DataSource dataSource;
 *
 *    @Override
 *    public void configure(AuthenticationManagerBuilder auth) throws Exception {
 *
 * 		auth.jdbcAuthentication()
 * 		.dataSource(dataSource)
 * 		.withDefaultSchema()
 * 		.withUser(User.withDefaultPasswordEncoder().username("user").password("password").roles("USER"));
 *    }
 * }
 *
 *
 *same thing by registering a JdbcUserDetailsManager bean
 * @Configuration
 * @EnableWebSecurity
 * public class SecurityConfig  {
 *
 * 	    @Autowired
 *    private DataSource dataSource;
 *
 *    @Bean
 *    JdbcUserDetailsManager jdbcUserDetailsManager(){
 *
 * 		UserDetails user = User.withDefaultPasswordEncoder()
 * 			.username("user")
 * 			.password("password")
 * 			.roles("USER").build();
 *
 * 		JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
 * 		jdbcUserDetailsManager.createUser(user);
 * 		return jdbcUserDetailsManager;
 *    }
 * }
 *
 *
 */
@Configuration
@EnableWebSecurity
@Slf4j
//@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final UserDetailServiceImpl userDetailService;

    //private final JwtFilter jwtFilter;

    private final JwtUtil jwtUtil;

    //@Autowired
    public SecurityConfig(UserDetailServiceImpl userDetailService, JwtUtil jwtUtil) {
        log.info("SecurityConfig constructor called");
        //this.jwtFilter = jwtFilter;
        this.jwtUtil = jwtUtil;
        this.userDetailService = userDetailService;
    }

    // /journal/** here ** is wildcard it means 0 or more characters

    /**
     * The securityFilterChain() method is a bean that defines the security filter chain.
     * The HttpSecurity parameter is used to configure the security settings for the application.
     * In this case, the method disables CSRF protection and authorizes requests based on their HTTP method and URL.
     * @param httpSecurity
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        log.info("SecurityConfig securityFilterChain() called");
        return httpSecurity
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/person/**").authenticated()
                        .anyRequest().permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                //.cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)  // Disabling CSRF
                //.httpBasic(Customizer.withDefaults())   // Replaces httpBasic() for Spring 6.1
                .addFilterBefore(getJwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        log.info("SecurityConfig passwordEncoder() called");
        return new BCryptPasswordEncoder();
    }

    /**
     * The authenticationManager() method is a bean that provides an AuthenticationManager.
     * It retrieves the authentication manager from the AuthenticationConfiguration instance.
     * @param authenticationConfiguration
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        log.info("SecurityConfig authenticationManager() called");
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        log.info("SecurityConfig userDetailsService()/userDetailsServiceImpl called");
        return userDetailService;
    }

    // A custom AuthenticationProvider bean is created to wire in the UserDetailsService and PasswordEncoder
    @Bean
    public AuthenticationProvider authenticationProvider() {
        log.info("SecurityConfig authenticationProvider() called");
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        log.info("SecurityConfig authenticationProvider() provider :: {}", provider);
        return provider;
    }

    @Bean
    @Lazy
    public JwtFilter getJwtFilter() {
        log.info("SecurityConfig getJwtFilter() called");
        return new JwtFilter(userDetailsService(),jwtUtil);
    }

}
