package com.inn.cafe.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {
//    private static final String SECRET_KEY = "btechdays";
    private static final String SECRET_KEY = "Xh7uTtZ4G2rzNhtMOg1lW3pKJ5zC8V6Lq9DvnQcFZxA";

    private static final long TOKEN_EXPIRATION = 1000 * 60 * 60 * 24; //24 HORAS

    public String extractUsername( String token ){
        return extractClaims(token, Claims::getSubject);
    }

    public Date extractExpiration( String token ){
        return extractClaims(token, Claims::getExpiration);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public <T> T extractClaims( String token, Function<Claims, T> claimsResolver ){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    private Boolean isTokenExpired( String token ) {
        return extractExpiration( token ).before( new Date() );
    }

    public String generateToken( String username, String role ) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken( claims, username );
    }

    private String createToken (Map<String, Object> claims, String subject){
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails ){
        final String username = extractUsername( token );
        return ( username.equals( userDetails.getUsername() ) && !isTokenExpired( token ) );
    }


}
