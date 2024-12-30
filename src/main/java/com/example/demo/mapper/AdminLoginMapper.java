package com.example.demo.mapper;

import com.example.demo.dto.security.User;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminLoginMapper {

    User findByUserId(String username);

}
