package org.wisestar.lottery.auth;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.wisestar.lottery.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangxu
 * @date 2017/10/25
 */
public final class JwtUserFactory {

    private JwtUserFactory() {
    }

    public static JwtUser create(User user) {
        return new JwtUser(
                user.getId().toString(),
                user.getOpenId(),
                user.getNickName(),
                user.getSessionKey(),
                user.getAuthority(),
                mapToGrantedAuthorities(user.getAuthority())
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(String authority) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (StringUtils.isNotEmpty(authority)) {
            for (String auth : StringUtils.split(authority, ",")) {
                authorities.add(new SimpleGrantedAuthority(auth));
            }
        }
        return authorities;
    }
}
