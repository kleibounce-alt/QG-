package org.example.dormitory.dto;

import lombok.Data;

/**
 * @author klei
 */
@Data

//登录
public class LoginRequest {
    private Long id;
    private String password;
}