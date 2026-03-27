package org.example.dormitory.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author klei
 */
@TableName("Student")
@Data
public class Student {
    private Long id;
    private String password;
    private String dormitory;
    @TableField("roomId")
    private String roomId;
}