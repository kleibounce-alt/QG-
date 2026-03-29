package org.example.dormitory.dto;

import lombok.Data;

/**
 * @author klei
 */
@Data

//登录
public class LoginRequest {
    private String id;
    private String password;
}