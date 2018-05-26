package org.wisestar.lottery.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangxu
 * @date 2017/10/20
 */
@Component
public class JwtTokenUtil implements Serializable {
    private static final long serialVersionUID = 6036432011809811566L;
    public static final String X_AUTHENTICATION_TOKEN = "X-Authentication-Token";

    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_CREATED = "created";
    private static final String CLAIM_KEY_NICKNAME = "nickname";
    private static final String CLAIM_KEY_AUTHORITY = "authority";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String getAuthorityFromToken(String token) {
        String authority;
        try {
            final Claims claims = getClaimsFromToken(token);
            authority = claims.get(CLAIM_KEY_AUTHORITY, String.class);
        } catch (Exception e) {
            authority = null;
        }
        return authority;
    }

    public String getNicknameFromToken(String token) {
        String nickname;
        try {
            final Claims claims = getClaimsFromToken(token);
            nickname = claims.get(CLAIM_KEY_NICKNAME, String.class);
        } catch (Exception e) {
            nickname = null;
        }
        return nickname;
    }

    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        JwtUser user = (JwtUser) userDetails;
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, user.getUsername());
        claims.put(CLAIM_KEY_CREATED, new Date());
        claims.put(CLAIM_KEY_NICKNAME, user.getNickname());
        claims.put(CLAIM_KEY_AUTHORITY, user.getAuthority());
        return generateToken(claims);
    }

    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    Boolean validateToken(String token, UserDetails userDetails) {
        JwtUser user = (JwtUser) userDetails;
        final String username = getUsernameFromToken(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }

}
