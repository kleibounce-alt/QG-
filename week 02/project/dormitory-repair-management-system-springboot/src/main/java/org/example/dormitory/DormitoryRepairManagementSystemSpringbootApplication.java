package org.example.dormitory;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * @author klei
 */
@SpringBootApplication
@MapperScan("org.example.dormitory.mapper")
public class DormitoryRepairManagementSystemSpringbootApplication {
    public static void main(String[] args) {
        SpringApplication.run(DormitoryRepairManagementSystemSpringbootApplication.class, args);

    }
}
