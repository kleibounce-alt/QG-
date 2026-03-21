package entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("Student")
@Data
public class Student {
    private long id;
    private String password;
    private String dormitory;
    private String roomId;
}
