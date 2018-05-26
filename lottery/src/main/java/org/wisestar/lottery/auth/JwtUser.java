package org.wisestar.lottery.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author zhangxu
 * @date 2017/10/25
 */
public class JwtUser implements UserDetails {

    private final String id;
    private final String username;
    private final String nickname;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String authority;

    public JwtUser(
            String id,
            String username,
            String nickname,
            String password,
            String authority,
            Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.authority = authority;
        this.authorities = authorities;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAuthority() {
        return authority;
    }
}
