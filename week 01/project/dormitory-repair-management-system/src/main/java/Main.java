import entity.Admin;
import entity.Student;
import mapper.AdminMapper;
import mapper.StudentMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import service.AdminService;
import service.StudentService;
import utils.MyBatisPlusSessionFactory;
import utils.intInput;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //退出页面的标志
        int sign = 0;
        while (true) {
            if (sign == 1) break;
            menu();
            Scanner sc = new Scanner(System.in);
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    login();
                    System.out.println("按回车键返回主菜单...");
                    sc.nextLine();
                    break;
                case 2:
                    register();
                    System.out.println("按回车键返回主菜单...");
                    sc.nextLine();
                    break;
                case 3:
                    sign = 1;
                    System.out.println("退出成功！");
                    break;
                default:
                    System.out.println("输入格式错误！请重新输入！");
                    System.out.println("按回车键返回主菜单...");
                    sc.nextLine();
                    break;
            }

        }
    }

    public static void menu() {
        System.out.println("===========================\n" +
                "宿舍报修管理系统\n" + "===========================\n"
        + "1. 登录\n" + "2. 注册\n" + "3. 退出\n" + "请选择操作（输入 1-3）：\n");
    }

    public static void login() {
        System.out.println("===== 用户登录 =====");
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入账号：");
        System.out.print(">");
        String account = sc.nextLine();
        System.out.println("请输入密码：");
        System.out.print(">");
        String password = sc.nextLine();
        Long id;
        try {
            //强制转换类型
            id = Long.parseLong(account);
        }
        catch (NumberFormatException e) {
            System.out.println("账号输入格式有误！请输入数字。");
            return;
        }
        SqlSessionFactory sqlSessionFactory =  MyBatisPlusSessionFactory.getSqlSessionFactory();
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            StudentMapper studentMapper = session.getMapper(StudentMapper.class);
            AdminMapper adminMapper = session.getMapper(AdminMapper.class);

            Student student = studentMapper.selectById(id);
            if (student != null && password.equals(student.getPassword())) {
                System.out.println("学生" + id + "登录成功！");
                StudentService.studentMenu(student);
                return;
            }

            Admin admin = adminMapper.selectById(id);
            if (admin != null && password.equals(admin.getPassword())) {
                System.out.println("管理员" + id + "登录成功！");
                AdminService.adminMenu(admin);
                return;
            }
            System.out.println("账号或密码错误！请重新输入");
            return ;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("数据库出错！");
        }
    }

    public static void register() {
        System.out.print("===== 用户注册 =====\n" + "请选择角色（1-学生，2-维修人员）：\n " + ">" );
        int choice = intInput.intInput(1, 2);
        switch (choice) {
            case 1:
                StudentService.register();
                break;
            case 2:
                AdminService.register();
                break;
            default:
                break;
        }
    }


}

