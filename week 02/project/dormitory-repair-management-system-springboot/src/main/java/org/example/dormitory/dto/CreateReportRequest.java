package org.example.dormitory.dto;

import lombok.Data;

/**
 * @author klei
 */
@Data

//创建表格
public class CreateReportRequest {
    private String deviceType;
    private String description;
}