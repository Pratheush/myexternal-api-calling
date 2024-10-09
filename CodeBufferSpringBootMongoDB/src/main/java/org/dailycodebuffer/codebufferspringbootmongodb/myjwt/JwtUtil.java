package org.dailycodebuffer.codebufferspringbootmongodb.myjwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * a Utility class named JwtUtil which provides methods for generating, validating, and extracting information
 * from JSON Web Tokens (JWTs) used for authentication in a Spring Boot application.
 *
 */
@Component
@Slf4j
public class JwtUtil {
    private final String SECRET_KEY = "TaK+HaV^uvCHEFsEVfypW#7g9^k*Z8$V/bd500164a7a15cb764660bb3780824b49343690aa9d0790e67c1e862396c1be1";

    private SecretKey getSigningKey() {
        log.info("JwtUtil getSigningKey() called");
        SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        log.info("JwtUtil getSigningKey() secretKey : {}",secretKey);
        return secretKey;
    }

    // get username from JWT token
    public String extractUsername(String token) {
        log.info("JwtUtil extractUsername() called");
        Claims claims = extractAllClaims(token);
        log.info("JwtUtil extractUsername() claims : {}",claims);
        String claimsSubject = claims.getSubject();
        log.info("JwtUtil extractUsername() claimsSubject OR USERNAME : {}",claimsSubject);
        return claimsSubject;
    }

    public Date extractExpiration(String token) {
        log.info("JwtUtil extractExpiration() called");
        Date expirationDate = extractAllClaims(token).getExpiration();
        log.info("JwtUtil extractExpiration() expirationDate : {}",expirationDate);
        return expirationDate;
    }

    private Claims extractAllClaims(String token) {
        log.info("JwtUtil extractAllClaims() called");
        Claims allExtractedClaims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        log.info("JwtUtil extractAllClaims() allExtractedClaims : {}",allExtractedClaims);
        return allExtractedClaims;
    }

    private Boolean isTokenExpired(String token) {
        log.info("JwtUtil isTokenExpired() called");
        boolean isTokenExpired = extractExpiration(token).before(new Date());
        log.info("JwtUtil isTokenExpired() isTokenExpired : {}",isTokenExpired);
        return isTokenExpired;
    }

    // generate JWT token
    public String generateToken(String username) {
        log.info("JwtUtil generateToken() called");
        Map<String, Object> claims = new HashMap<>();
        String tokenGenerated = createToken(claims, username);
        log.info("JwtUtil generateToken() tokenGenerated : {}",tokenGenerated);
        log.info("JwtUtil generateToken() claims : {}",claims);
        return tokenGenerated;
    }

    /**
     * The createToken(Authentication authentication) method generates a new JWT based on the provided Authentication object or here we supplied Subject i.e. Username and HashMap,
     * which contains information about the user being authenticated. It uses the Jwts.builder() method to create a new JwtBuilder object,
     * sets the subject (i.e., username) of the JWT, the issue date, and expiration date, and signs the JWT using the getSigningKey() method.
     * Finally, it returns the JWT as a string.
     * @param claims
     * @param subject
     * @return
     */
    private String createToken(Map<String, Object> claims, String subject) {
        log.info("JwtUtil createToken() called");
        String tokenCreated = Jwts.builder()
                .claims(claims)
                .subject(subject)
                .header().empty().add("typ", "JWT")
                .and()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 5 minutes expiration time or 1hr expiration time
                .signWith(getSigningKey())
                .compact();
        log.info("JwtUtil createToken() tokenCreated : {}",tokenCreated);
        return tokenCreated;
    }

    /**
     * validateToken(String token) method validates the provided JWT. It uses the Jwts.parser() method to create a new JwtParserBuilder object,
     * verify the signing key using the getSigningKey() method and parses the JWT using the parse() method.
     * If the JWT is valid, the method returns true. If the JWT is invalid or has expired, the method logs an error message using the logger object and returns false.
     * @param token
     * @return
     */
    // validate JWT token
    public Boolean validateToken(String token) {
        log.info("JwtUtil validateToken() called");
        /*boolean validToken = !isTokenExpired(token);
        log.info("JwtUtil validateToken() validToken : {}",validToken);
        return validToken;*/

        try{
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    //.setSigningKey(getSigningKey())
                    .build()
                    .parse(token);

            boolean validToken = !isTokenExpired(token);
            log.info("JwtUtil validateToken() validToken : {}",validToken);
            return validToken;

            //return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

}
