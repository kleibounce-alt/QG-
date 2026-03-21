package entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("Admin")
@Data
public class Admin {
    private long id;
    private String password;
}
