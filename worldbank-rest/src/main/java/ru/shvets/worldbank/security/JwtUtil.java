package ru.shvets.worldbank.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.shvets.worldbank.util.JwtAuthenticationException;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret.access}")
    private String secretAccessKey;

    @Value("${jwt.secret.refresh}")
    private String secretRefreshKey;

    @Value("${jwt.expiration.access}")
    private Integer expirationAccess;

    @Value("${jwt.expiration.refresh}")
    private Integer expirationRefresh;

    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtUtil(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, Boolean isRefresh) {
        final Claims claims = extractAllClaims(token, isRefresh);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token, Boolean isRefresh) {
        try {
            if (isRefresh)
                return Jwts.parser().setSigningKey(secretRefreshKey).parseClaimsJws(token).getBody();
            return Jwts.parser().setSigningKey(secretAccessKey).parseClaimsJws(token).getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("JWT token is expired or invalid");
        }
    }

    public String extractUsername(String token, Boolean isRefresh) {
        return extractClaim(token, Claims::getSubject, isRefresh);
    }

    private boolean isTokenExpired(String token, Boolean isRefresh) {
        return extractClaim(token, Claims::getExpiration, isRefresh).before(new Date());
    }

    public boolean isTokenValid(String token, Boolean isRefresh) {
        try {
            return !isTokenExpired(token, isRefresh);
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("JWT token is expired or invalid");
        }
    }

    public String generateAccessToken(UserDetails userDetails) {
        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationAccess * 1000))
                .signWith(SignatureAlgorithm.HS256, secretAccessKey)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationRefresh * 1000))
                .signWith(SignatureAlgorithm.HS256, secretRefreshKey)
                .compact();
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(extractUsername(token, false));
            if (!userDetails.isAccountNonLocked())
                throw new LockedException("");
            return new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(), null, userDetails.getAuthorities());
        } catch (UsernameNotFoundException | LockedException e) {
            throw new JwtAuthenticationException("JWT token is expired or invalid");
        }
    }

    public String getToken(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return null;
        final String token = authHeader.substring(7);
        return token;
    }
}
