package entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("report")
@Data
public class report {
    private int id;
    private Long studentId;
    private String deviceType;
    private String description;
    private String status;
}
