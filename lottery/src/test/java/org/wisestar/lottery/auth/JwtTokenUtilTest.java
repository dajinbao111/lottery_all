package org.wisestar.lottery.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;
import org.wisestar.lottery.util.DateUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class JwtTokenUtilTest {

    @Test
    public void test() throws Exception {
        String username = "zhangxu";

        Map<String, Object> map = new HashMap<>();
        map.put("sub", username);

        String token = Jwts.builder()
                .setClaims(map)
                .setExpiration(new Date(System.currentTimeMillis() + 5 * 1000))
                .signWith(SignatureAlgorithm.HS512, "abc123")
                .compact();
        System.out.println(DateUtils.date2String(new Date(), "yyyy-MM-dd HH:mm:ss"));
        System.out.println(token);

        TimeUnit.SECONDS.sleep(1);

        Claims claims = Jwts.parser()
                .setSigningKey("abc123")
                .parseClaimsJws(token)
                .getBody();

        System.out.println(claims.getSubject());

        //过期后解析失败
//        Date expiration = claims.getExpiration();
//        System.out.println(DateUtils.date2String(expiration, "yyyy-MM-dd HH:mm:ss"));

//        TimeUnit.SECONDS.sleep(8);
//        Claims claims = Jwts.parser()
//                .setSigningKey("abc123")
//                .parseClaimsJws(token)
//                .getBody();
//        Date expiration = claims.getExpiration();
//        expiration.before(new Date());


    }
}