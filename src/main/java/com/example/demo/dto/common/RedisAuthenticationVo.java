package com.example.demo.dto.common;

import com.example.demo.dto.security.Role;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RedisAuthenticationVo {

    private String userId;
    private String password;
    private String username;
    private Role role;
    private String refreshToken;

}
