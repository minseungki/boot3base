package com.example.demo.security;

import com.example.demo.dto.common.RedisAuthenticationVo;
import com.example.demo.dto.security.User;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.mapper.LoginMapper;
import com.example.demo.util.RedisUtil;
import com.example.demo.util.RestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final LoginMapper mapper;
    private final RedisUtil redisUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        RedisAuthenticationVo redisUserInfo = redisUtil.getRedisUserInfo(username);
        if (ObjectUtils.isEmpty(redisUserInfo)) {
            // mapper 조회
            user = mapper.findByUserId(username);
        } else {
            // Redis에 저장된 회원 정보 사용
            user = RestUtil.convert(redisUserInfo, User.class);
        }

        if (user != null) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
            return new PrincipalDetails(user);
        } else {
            throw new UserNotFoundException();
        }
    }

}

