package org.example.dormitory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author klei
 */
@Data
@TableName("report_image")
public class ReportImage {
    private Long id;
    private Long reportId;
    private String imagePath;
    private String originalName;
    private Long createTime;
}