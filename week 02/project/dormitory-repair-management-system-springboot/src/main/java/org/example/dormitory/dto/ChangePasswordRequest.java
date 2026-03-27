package org.example.dormitory.dto;

import lombok.Data;

/**
 * @author klei
 */
@Data

public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
}
