package com.example.demo.mapper;

import com.example.demo.dto.login.*;
import com.example.demo.dto.security.User;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginMapper {

    User findByUserId(String username);
    LoginSelectResponse selectLogin(LoginRequest req);
    LoginSelectResponse selectMember(LoginRequest req);

}
