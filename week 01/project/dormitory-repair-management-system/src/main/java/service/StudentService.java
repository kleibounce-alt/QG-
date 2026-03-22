package service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import entity.Student;
import mapper.StudentMapper;
import mapper.reportMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import utils.MyBatisPlusSessionFactory;
import utils.intInput;
import entity.report;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
            String line = sc.nextLine().trim();   // 读取整行并去除首尾空白

            if (line.isEmpty()) {
                System.out.println("不可输入空白！");
                continue;
            }

            try {
                long input = Long.parseLong(line);
                String tempNum = Long.toString(input);
                if (tempNum.matches("^(3125|3225)\\d{6}$")) {
                    studentId = input;
                    break;
                } else {
                    System.out.println("前缀必须为3125或3225且长度为十！");
                }
            } catch (NumberFormatException e) {
                System.out.println("请输入有效的长整数！");
            }
        }
        Scanner sc2 = new Scanner(System.in);
        System.out.println("请输入密码：");
        String studentPassword;
        while (true) {
            System.out.print(">");
            String line = sc2.nextLine();
            if (line.trim().isEmpty()) {
                System.out.println("不可输入空白！");
                continue;
            }
            studentPassword = line;
            break;
        }
        Scanner sc3 = new Scanner(System.in);
        System.out.println("请再输入一次密码：");
        while (true) {
            System.out.print(">");
            String twicePassword = sc3.nextLine();
            if (twicePassword.equals(studentPassword)) {
                break;
            } else {
                System.out.println("与原密码不符，请重新输入！");
            }
        }
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
        while (true) {
            System.out.println("===== 学生菜单 =====\n" + "1. 绑定/修改宿舍\n" + "2. 创建报修单\n" +
                    "3. 查看我的报修记录\n" + "4. 取消报修单\n" + "5. 修改密码\n" + "6. 退出");
            System.out.println("请选择操作（输入 1-6）：");
            System.out.print(">");
            int choice = intInput.intInput(1, 6);
            switch (choice) {
                case 1:
                    dormitoryService(student);
                    break;
                case 2:
                    createReport(student);
                    break;
                case 3:
                    lookupReports(student);
                    break;
                case 4:
                    cancelReport(student);
                    break;
                case 5:
                    changeId(student);
                    break;
                case 6:
                    System.out.println("退出成功！");
                    return;
                default:
                    break;
            }
        }
    }

    //绑定或修改宿舍
    public static void dormitoryService(Student student) {
            String curDorm = student.getDormitory();
            String curRoom = student.getRoomId();
            boolean isExist;
            if (curDorm.equals("")) isExist = false;
            else isExist = true;

            if (!isExist) {
                System.out.println("当前为绑定宿舍，请进行绑定：");
                System.out.print(">");
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
            System.out.println("请输入新宿舍号(例如：325室)：");
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

    //创造报修单
    public static void createReport(Student student) {
            Scanner sc = new Scanner(System.in);
            System.out.println("===== 创建报修单 =====");
            System.out.print("请输入设备类型（例如：空调、热水器、灯管等）: ");
            String deviceType;
            while (true) {
                System.out.print(">");
                if (sc.hasNextLine()) {
                    deviceType = sc.nextLine();
                    break;
                }
                else System.out.println("输入不能为空！");
            }
            Scanner sc2 = new Scanner(System.in);
            System.out.println("请输入故障描述:");
            String description;
            while (true) {
                System.out.print(">");
                if (sc2.hasNextLine()) {
                    description = sc2.nextLine();
                    break;
                }
                else System.out.println("输入不能为空！");
            }

            report report = new report();
            report.setStudentId(student.getId());
            report.setDeviceType(deviceType);
            report.setDescription(description);
            report.setStatus("未完成");

            //获取当前时间
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String currentTime = now.format(formatter);
            report.setTime(currentTime);

            SqlSessionFactory sqlSessionFactory =  MyBatisPlusSessionFactory.getSqlSessionFactory();
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                reportMapper mapper = session.getMapper(reportMapper.class);
                int row = mapper.insert(report);
                if (row > 0) {
                    System.out.println("报修单创建成功！单号：" + report.getId());
                    return;
                }
                else {
                    System.out.println("创建失败，请重试！");
                    return;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("创建失败！数据库有误！");
            }
    }

    //查看报修表
    public static void lookupReports(Student student) {
        SqlSessionFactory sqlSessionFactory =  MyBatisPlusSessionFactory.getSqlSessionFactory();
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            reportMapper mapper = session.getMapper(reportMapper.class);

            List<report> reports = mapper.selectList(new QueryWrapper<report>().eq("studentId", student.getId()));
            if (reports.isEmpty()) {
                System.out.println("暂无报修单！");
                return ;
            }

            System.out.println("===== 您的报修单 =====");
            for (report report : reports) {
                System.out.println("单号：" + report.getId());
                System.out.println("设备类型：" + report.getDeviceType());
                System.out.println("故障描述：" + report.getDescription());
                System.out.println("状态：" + (report.getStatus()));
                System.out.println("----------------------------");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("数据库有误！");
        }
    }

    //取消报修单
    public static void cancelReport(Student student) {
        SqlSessionFactory sqlSessionFactory =  MyBatisPlusSessionFactory.getSqlSessionFactory();
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            reportMapper mapper = session.getMapper(reportMapper.class);

            List<report> reports = mapper.selectList(new QueryWrapper<report>().eq("studentId", student.getId()).eq("status", "active"));

            if (reports.isEmpty()) {
                System.out.println("当前暂无可取消的报修单！");
                return ;
            }

            //查询所有active保修单
            System.out.println("===== 可取消的报修单 =====");
            for (report report : reports) {
                System.out.println("单号：" + report.getId() + "| 设备类型：" + report.getDeviceType() + "| 描述：" + report.getDescription());
            }
            System.out.print("请输入要取消的报修单号（输入 0 返回）: ");

            int len = reports.size();
            int reportId;
            reportId = intInput.intInput(0, len);
            if (reportId == 0) {
                System.out.println("返回成功！");
                return ;
            }

            report rp = mapper.selectById(reportId);
            if (!rp.getStudentId().equals(student.getId())) {
                System.out.println("该报修单不属于您的！");
                return ;
            }
            int r = mapper.deleteById(rp);
            if (r > 0) {
                System.out.println("取消成功！");
            }
            else {
                System.out.println("取消失败！");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("数据库出错！无法取消报修单。。");
        }
    }

    //修改密码
    public static void changeId(Student student) {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入新密码：");
        String password1;
        while (true) {
            System.out.print(">");
            if (sc.hasNextLine()) {
                password1 = sc.nextLine();
                if (password1.equals("")) {
                    System.out.println("不可输入空白！");
                    continue;
                }
                break;
            }
        }

        Scanner sc2 = new Scanner(System.in);
        System.out.println("再次确认密码：");
        String password2 = "";
        while (true) {
            System.out.print(">");
            if (sc2.hasNextLine()) {
                password2 = sc2.nextLine();
            }
            if (password2.isEmpty()) {
                System.out.println("不可输入空白！");
                continue;
            }
            if (password1.equals(password2)) {
                break;
            }
        }
        System.out.println("确认修改？ (① - 是  ② - 否)");
        int choice = intInput.intInput(1, 2);
        if (choice == 1) {
            SqlSessionFactory sqlSessionFactory =  MyBatisPlusSessionFactory.getSqlSessionFactory();
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                StudentMapper mapper = session.getMapper(StudentMapper.class);

                student.setPassword(password1);
                int r = mapper.updateById(student);
                if (r > 0) {
                    System.out.println("密码修改成功！");
                } else {
                    System.out.println("密码修改失败！");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("数据库出错！修改失败！");
            }
        }
        else if (choice == 2) {
            System.out.println("取消成功！");
            return;
        }
    }
}
