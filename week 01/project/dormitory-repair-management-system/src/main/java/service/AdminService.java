package service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import entity.Admin;
import entity.Student;
import mapper.AdminMapper;
import mapper.StudentMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import utils.MyBatisPlusSessionFactory;

import java.util.Scanner;

public class AdminService {
    public static void register() {
        System.out.println("请输入工号(前缀为0025，长度为十位整数)：");
        Scanner sc = new Scanner(System.in);
        Long adminId;
        //注册工号
        while (true) {
            System.out.print(">");
            if (sc.hasNext()) {
                System.out.println("不可输入空白！");
            }
            else {
                //防止出现空白和非数字的情况
                if (sc.hasNextLong()) {
                    long input = sc.nextLong();
                    String tempNum = Long.toString(input);
                    //正则表达式控制长度和前缀
                    if (tempNum.matches("^(0025)\\d{6}$")) {
                        adminId = input;
                        break;
                    }
                    else {
                        sc.next();
                        System.out.println("前缀必须为0025且长度为十！");
                    }
                }
                else {
                    //清空sc
                    sc.next();
                    System.out.println("请输入有效的长整数！");
                }
            }
        }
        //吸收换行符
        sc.nextLine();
        System.out.println("请输入密码：");
        String adminPassword;
        while (true) {
            System.out.print(">");
            if (sc.hasNext()) {
                adminPassword = sc.nextLine();
                break;
            }
            else {
                sc.next();
                System.out.println("不可输入空白！");
            }
        }

        sc.nextLine();
        System.out.println("请再输入一次密码：");
        String twicePassword;
        while (true) {
            twicePassword = sc.nextLine();
            if (twicePassword.equals(adminPassword)) {
                break;
            }
            else {
                sc.next();
                System.out.println("与原密码不符，请重新输入！");
            }
        }
        System.out.println("注册成功！请返回主页面登录！\n");

        adminIfExist(adminId, adminPassword);
    }

    public static void adminIfExist(Long id, String password) {
        SqlSessionFactory sqlSessionFactory =  MyBatisPlusSessionFactory.getSqlSessionFactory();
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            AdminMapper mapper = session.getMapper(AdminMapper.class);

            QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", id);

            Long count =  mapper.selectCount(queryWrapper);
            if (count > 0) {
                System.out.println("该工号已存在！");
                return;
            }
            Admin admin = new Admin();
            admin.setId(id);
            admin.setPassword(password);
            mapper.insert(admin);
            System.out.println("注册成功！");
        }
    }
}

