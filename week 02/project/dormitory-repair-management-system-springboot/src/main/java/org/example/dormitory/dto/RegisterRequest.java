package org.example.dormitory.dto;

import lombok.Data;

/**
 * @author klei
 */
@Data

//注册
public class RegisterRequest {
    private String id;
    private String password;
}
