package entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("report")
@Data
public class report {
    private int id;
    @TableField("studentId")
    private Long studentId;
    @TableField("deviceType")
    private String deviceType;
    private String description;
    private String status;
    private String time;
}
