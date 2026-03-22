package service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import entity.Admin;
import entity.Student;
import entity.report;
import mapper.AdminMapper;
import mapper.StudentMapper;
import mapper.reportMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import sun.security.mscapi.CPublicKey;
import utils.MyBatisPlusSessionFactory;
import utils.intInput;

import java.util.List;
import java.util.Scanner;

public class AdminService {
    public static void register() {
        System.out.println("请输入工号(前缀为0025，长度为十位整数)：");
        Scanner sc = new Scanner(System.in);
        Long adminId;
        //注册工号
        while (true) {
            System.out.print(">");
            String line = sc.nextLine().trim();

            if (line.isEmpty()) {
                System.out.println("不可输入空白！");
                continue;
            }

            // 直接校验字符串格式
            if (line.matches("0025\\d{6}")) {
                try {
                    // 格式正确后再转为Long存储，因为有前导0
                    adminId = Long.parseLong(line);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("数字转换异常，请检查输入！");
                }
            } else {
                System.out.println("工号格式错误！必须为十位数字，且以0025开头（例如0025110011）");
            }
        }
        Scanner sc2 = new Scanner(System.in);
        System.out.println("请输入密码：");
        String adminPassword;
        while (true) {
            System.out.print(">");
            String line = sc2.nextLine();
            if (line.trim().isEmpty()) {
                System.out.println("不可输入空白！");
                continue;
            }
            adminPassword = line;
            break;
        }
        Scanner sc3 = new Scanner(System.in);
        System.out.println("请再输入一次密码：");
        while (true) {
            System.out.print(">");
            String twicePassword = sc3.nextLine();
            if (twicePassword.equals(adminPassword)) {
                break;
            } else {
                System.out.println("与原密码不符，请重新输入！");
            }
        }
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

    public static void adminMenu(Admin admin) {
        while (true) {
            System.out.println("===== 管理员菜单 =====\n" + "1. 查看所有报修单\n" + "2. 查看报修单详情\n" + "3. 更新报修单状态\n" + "4. 删除报修单\n" +
                    "5. 修改密码\n" + "6. 退出\n" + "请选择操作（输入 1-6）：");
            int choice = intInput.intInput(1, 6);
            switch (choice) {
                case 1:
                    lookupAllReport();
                    break;
                case 2:
                    viewReport();
                    break;
                case 3:
                    updateReport();
                    break;
                case 4:
                    deleteReport();
                    break;
                case 5:
                    changeId(admin);
                    break;
                case 6:
                    System.out.println("退出成功！");
                    return;
                default:
                    break;
            }
        }
    }

    //查看所有报修单
    public static void lookupAllReport() {
        SqlSessionFactory sqlSessionFactory =  MyBatisPlusSessionFactory.getSqlSessionFactory();
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            reportMapper mapper = session.getMapper(reportMapper.class);

            List<report> reports = mapper.selectList(null);
            System.out.println("===== 所有报修单 =====");
            if (reports.isEmpty()) {
                System.out.println("暂时无报修单。");
                return;
            }
            for (report report : reports) {
                System.out.println("单号：" + report.getId() + " || 报修人：" + report.getStudentId());
            }
        }
    }

    //查看某个报修单详情
    public static void viewReport() {
        int len;
        SqlSessionFactory sqlSessionFactory =  MyBatisPlusSessionFactory.getSqlSessionFactory();
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            reportMapper mapper = session.getMapper(reportMapper.class);

            List<report> reports = mapper.selectList(null);
            len = reports.size();
            if (len == 0) {
                System.out.println("暂无报修单。");
                return;
            }

            System.out.println("请输入报修单的Id" + "(1 ~ " + len + ")：");
            int choice = intInput.intInput(1, len);
            report report = mapper.selectById(choice);
            System.out.println("单号：" + report.getId() + "\n报修人：" + report.getStudentId() +
                    "\n设备类型：" + report.getDeviceType() + "\n描述：" + report.getDescription() + "\n状态" + report.getStatus());
            return;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("数据库出错！");
            return;
        }
    }

    public static void updateReport() {
        SqlSessionFactory sqlSessionFactory =  MyBatisPlusSessionFactory.getSqlSessionFactory();
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            reportMapper mapper = session.getMapper(reportMapper.class);
            List<report> reports = mapper.selectList(null);
            if (reports.isEmpty()) {
                System.out.println("暂无报修单。");
                return;
            }

            System.out.println("===== 所有报修单状态 =====");
            for (report report : reports) {
                System.out.println("单号：" + report.getId() + " || 状态：" + report.getStatus());
            }

            System.out.println("请输入要更新的报修单Id(1 ~ " +  reports.size() + ")：");
            int choice = intInput.intInput(1, reports.size());

            report report = mapper.selectById(choice);
            System.out.println("确认将单号" + report.getId() + "的报修单状态改变？ ① - 是  ② - 否");
            int choice2 = intInput.intInput(1, 2);
            int r;
            if (choice2 == 1) {
                String status = report.getStatus();
                if (status.equals("active")) {
                    report.setStatus("inactive");
                    r = mapper.updateById(report);
                }
                else {
                    report.setStatus("active");
                    r = mapper.updateById(report);
                }

                if (r > 0) {
                    System.out.println("更新状态成功！");
                    return;
                }
                System.out.println("更新失败！");
                return;
            }
            else {
                System.out.println("返回成功！");
                return;
            }
        }
    }

    //删除报修单
    public static void deleteReport() {
        SqlSessionFactory sqlSessionFactory =  MyBatisPlusSessionFactory.getSqlSessionFactory();
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            reportMapper mapper = session.getMapper(reportMapper.class);
            List<report> reports = mapper.selectList(null);
            if (reports.isEmpty()) {
                System.out.println("暂无报修单！");
                return;
            }

            System.out.println("===== 所有报修单 =====");
            for (report report : reports) {
                System.out.println("单号：" + report.getId());
            }
            System.out.println("请选择要删除的单号(输入0取消操作)：");
            int choice = intInput.intInput(0, reports.size());
            if (choice == 0) {
                System.out.println("取消成功！");
                return;
            }
            int r = mapper.deleteById(choice);
            if (r > 0) {
                System.out.println("删除成功!");
            }
            else {
                System.out.println("删除失败！");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("数据库出错！");
            return;
        }
    }

    public static void changeId(Admin admin) {
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
                AdminMapper mapper = session.getMapper(AdminMapper.class);

                admin.setPassword(password1);
                int r = mapper.updateById(admin);
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

