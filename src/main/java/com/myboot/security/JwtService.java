package com.myboot.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private final KeyPair keyPair;
    private static final String ID = "id";
    private static final String EMAIL = "email";
    private static final String ROLE = "role";

    public JwtService() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(Authentication.RSA.name());
        keyPairGenerator.initialize(2048);
        keyPair = keyPairGenerator.generateKeyPair();
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof UserSecurity customUserDetails) {
            claims.put(ID, customUserDetails.getId());
            claims.put(EMAIL, customUserDetails.getEmail());
            claims.put(ROLE, customUserDetails.getRole());
        }
        return generateToken(claims, userDetails);
    }
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder().claims(extraClaims).
                subject(userDetails.getUsername()).
                issuedAt(new Date(System.currentTimeMillis())).
                expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))// one day
                .signWith(keyPair.getPrivate()).compact();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = Jwts.parser().verifyWith(keyPair.getPublic())
                .build().parseSignedClaims(token).getPayload();
        return claimsResolvers.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractClaim(token, Claims::getSubject);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
}
