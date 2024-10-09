package org.dailycodebuffer.codebufferspringbootmongodb.myjwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 *  Execute Before Executing Spring Security Filters
 *  Validate the JWT Token and Provides user details to Spring Security for Authentication
 *
 * A JwtFilter class in a Spring Boot application that intercepts incoming HTTP requests and validates
 * JWT tokens that are included in the Authorization header. If the token is valid, the filter sets the current
 * user's authentication in the SecurityContext.
 *
 */
@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter{

    private final UserDetailsService userDetailsService;

    private final JwtUtil jwtUtil;

    @Autowired
    //@Lazy
    public JwtFilter(UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        log.info("JwtFilter Constructor Called");
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * The class extends the Spring framework's OncePerRequestFilter, which ensures that the filter is only applied once per request.
     *
     * The doFilterInternal method is the main logic of the filter. It extracts the JWT token from the Authorization header
     *  and validates the token using the JwtUtil class, and sets the authentication information in the SecurityContextHolder.
     *
     *  The SecurityContextHolder is used to store the authentication information for the current request. In this case,
     *  the filter sets a UsernamePasswordAuthenticationToken with the UserDetails and authorities associated with the token.
     * @param request
     * @param response
     * @param chain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        log.info("JwtFilter doFilterInternal() Called");
        String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            log.info("JwtFilter doFilterInternal() if Block (authorizationHeader != null && authorizationHeader.startsWith(\"Bearer \")) Called");
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }
        if (username != null) {
            log.info("JwtFilter doFilterInternal() if Block (username != null) Called");
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(jwt)) {
                log.info("JwtFilter doFilterInternal() if Block (jwtUtil.validateToken(jwt)) Called");
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        chain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request){
        log.info("JwtFilter getTokenFromRequest() Called");
        String bearerToken = request.getHeader("Authorization");

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }

        return null;
    }
}
