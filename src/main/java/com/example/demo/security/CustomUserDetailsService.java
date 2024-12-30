package com.example.demo.security;

import com.example.demo.dto.security.Role;
import com.example.demo.dto.security.User;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.mapper.AdminLoginMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminLoginMapper adminLoginMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // mapper 조회
        User user = adminLoginMapper.findByUserId(username);

        if (user != null) {
            user.setRole(Role.USER);
            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            String role = user.getRole().name();
            authorities.add(new SimpleGrantedAuthority(role));
            return new PrincipalDetails(user);
        } else {
            throw new UserNotFoundException();
        }
    }

}

