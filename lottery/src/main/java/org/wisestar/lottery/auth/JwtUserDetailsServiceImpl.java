package org.wisestar.lottery.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.wisestar.lottery.entity.User;
import org.wisestar.lottery.mapper.UserMapper;

/**
 * @author zhangxu
 * @date 2017/10/25
 */
@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.getByOpenId(username);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with openid '%s'.", username));
        } else {
            return JwtUserFactory.create(user);
        }
    }
}
