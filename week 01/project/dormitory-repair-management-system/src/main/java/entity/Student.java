package entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("Student")
@Data
public class Student {
    private Long id;
    private String password;
    private String dormitory;
    @TableField("roomId")
    private String roomId;
}
