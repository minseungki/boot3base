package com.example.demo.dto.common;

import com.example.demo.dto.common.enumeration.*;
import com.example.demo.dto.security.Role;
import lombok.*;

import java.util.Date;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtAuthenticationVo {

    private Long seq;
    private String id;
    private PlatformType platformType; // front, admin
    private JwtType jwtType; // access token, refresh token
    private ProfileType profile; // dev, prod
    private Date expiration;
    private Role role;

}
