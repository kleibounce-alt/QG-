package service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import entity.Student;
import mapper.StudentMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import utils.MyBatisPlusSessionFactory;
import utils.intInput;

import java.util.Scanner;

public class StudentService {
    //学生注册
    public static void register() {
        System.out.println("请输入学号(前缀为3125或3225，长度为十位整数)：");
        Scanner sc = new Scanner(System.in);
        Long studentId;
        //注册学号
        while (true) {
            System.out.print(">");
            if (sc.hasNext()) {
                System.out.println("不可输入空白！");
            }
            else {
                //防止出现空白和非数字的情况
                if (sc.hasNextLong()) {
                    Long input = sc.nextLong();
                    String tempNum = Long.toString(input);
                    //正则表达式控制长度和前缀
                    if (tempNum.matches("^(3125|3225)\\d{6}$")) {
                        studentId = input;
                        break;
                    }
                    else {
                        sc.next();
                        System.out.println("前缀必须为3125或3225且长度为十！");
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
        String studentPassword;
        while (true) {
            System.out.print(">");
            if (sc.hasNext()) {
                studentPassword = sc.nextLine();
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
            if (twicePassword.equals(studentPassword)) {
                break;
            }
            else {
                sc.next();
                System.out.println("与原密码不符，请重新输入！");
            }
        }
        System.out.println("注册成功！请返回主页面登录！\n");

        studentIfExist(studentId, studentPassword);
    }

    //检测账号是否注册过
    public static void studentIfExist(Long id, String password) {
        SqlSessionFactory sqlSessionFactory =  MyBatisPlusSessionFactory.getSqlSessionFactory();
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            StudentMapper mapper = session.getMapper(StudentMapper.class);

            QueryWrapper <Student> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", id);

            Long count =  mapper.selectCount(queryWrapper);
            if (count > 0) {
                System.out.println("该学号已存在！");
                return;
            }
            Student student = new Student();
            student.setId(id);
            student.setPassword(password);
            mapper.insert(student);
            System.out.println("注册成功！");
        }
    }

    //学生菜单
    public static void studentMenu(Student student) {
        System.out.println("===== 学生菜单 =====\n" + "1. 绑定/修改宿舍\n" + "2. 创建报修单\n" +
                "3. 查看我的报修记录\n" + "4. 取消报修单\n" + "5. 修改密码\n" + "6. 退出");
        System.out.println("请选择操作（输入 1-6）：\n" + ">");
        int choice = intInput.intInput(1, 6);
        switch (choice) {
            case 1:
                dormitoryService(student);
                break;
            case 2:

                break;
        }
    }

    //绑定或修改宿舍
    public static void dormitoryService(Student student) {
            String curDorm = student.getDormitory();
            String curRoom = student.getRoomId();
            boolean isExist;
            if (curDorm == null) isExist = false;
            else isExist = true;

            if (!isExist) {
                System.out.println("当前为绑定宿舍，请进行绑定：" + ">");
            }
            else {
                System.out.println("当前宿舍为：" +  curDorm + "-" +  curRoom);
                System.out.print("是否修改宿舍？ (① 是 ,  ② 否)：");
                int choice = intInput.intInput(1, 2);
                if (choice == 2) {
                    System.out.println("已取消修改！");
                    return;
                }
            }

            System.out.println("请输入新宿舍楼栋(例如：西1)：");
            Scanner sc = new Scanner(System.in);
            String newDorm = sc.nextLine();
            if (newDorm == null || newDorm.trim().isEmpty()) {
                System.out.println("宿舍楼栋不能为空！");
                return;
            }
            String newRoom = sc.nextLine();
            if (newRoom == null || newRoom.trim().isEmpty()) {
                System.out.println("宿舍号不能为空！");
                return;
            }

            SqlSessionFactory sqlSessionFactory =  MyBatisPlusSessionFactory.getSqlSessionFactory();
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                StudentMapper mapper = session.getMapper(StudentMapper.class);

                student.setDormitory(newDorm);
                student.setRoomId(newRoom);

                int row = mapper.updateById(student);
                if (row > 0) {
                    if (!isExist) {
                        System.out.println("绑定成功！");
                    }
                    else {
                        System.out.println("修改成功！");
                    }
                    return;
                }
                else {
                    if (!isExist) {
                        System.out.println("绑定失败！");
                    }
                    else {
                        System.out.println("修改失败！");
                    }
                    return;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("操作失败，是数据库异常！");
                return;
            }
    }

    public static void createReport(Student student) {

    }
}
