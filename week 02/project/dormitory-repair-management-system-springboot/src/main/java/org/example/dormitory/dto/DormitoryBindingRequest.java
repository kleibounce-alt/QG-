package org.example.dormitory.dto;

import lombok.Data;

/**
 * @author klei
 */
@Data

//学生宿舍绑定
public class DormitoryBindingRequest {
    private String dormitory;
    private String roomId;
}