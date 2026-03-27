package org.example.dormitory.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author klei
 */
@TableName("report")
@Data
public class report {
    private Integer id;
    @TableField("studentId")
    private Long studentId;
    @TableField("deviceType")
    private String deviceType;
    private String description;
    private String status;
    private String time;

    @Override
    public String toString() {
        return "报修单ID ：" + id +
                "\n学生ID：" + studentId +
                "\n设备类型：" + deviceType +
                "\n问题描述：" + description +
                "\n报修单状态：" + status +
                "\n最新更新时间：" + time;
    }
}