package org.example.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.dormitory.dto.*;
import org.example.dormitory.entity.Student;
import org.example.dormitory.entity.report;
import org.example.dormitory.mapper.StudentMapper;
import org.example.dormitory.mapper.reportMapper;
import org.example.dormitory.service.StudentService;
import org.example.dormitory.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.dormitory.config.SecurityConfig;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author klei
 */
@Service
//自动生成日志对象
@Slf4j
@Transactional
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private reportMapper reportMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    //注册
    public void register(RegisterRequest request) {
        String id = String.valueOf(request.getId());
        if (!id.matches("^(3125 | 3225) \\d{6}$")) {
            throw new IllegalArgumentException("学号前缀必须为3125或3225且长度为10位");
        }

        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        long count = studentMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new IllegalArgumentException("该学号已存在");
        }

        Student student = new Student();
        student.setId(request.getId());
        //进行加密
        student.setPassword(passwordEncoder.encode(request.getPassword()));
        student.setDormitory("");
        student.setRoomId("");
        studentMapper.insert(student);
        log.info("学生注册成功：{}", request.getId());
    }

    @Override
    //登录
    public String login(LoginRequest request) {
        Student student = getStudentById(request.getId());
        if (!passwordEncoder.matches(request.getPassword(), student.getPassword())) {
            throw new IllegalArgumentException("学号或密码错误");
        }

        // 生成 JWT
        String token = jwtUtils.generateToken(String.valueOf(student.getId()),"ROLE_STUDENT");
        log.info("学生登录成功：{}", student.getId());
        return token;
    }

    @Override
    //绑定/修改宿舍
    public void bindDormitory(Long studentId, DormitoryBindingRequest request) {
        Student student = getStudentById(studentId);
        student.setDormitory(request.getDormitory());
        student.setRoomId(request.getRoomId());

        int r = studentMapper.updateById(student);
        if (r > 0) {
            log.info("学生 {} 绑定宿舍成功：{}-{}", studentId, request.getDormitory(), request.getRoomId());
        }
        else {
            throw new IllegalArgumentException("绑定/修改宿舍失败");
        }
    }

    @Override
    public void createReport(Long studentId, CreateReportRequest request) {
        report report = new report();

        report.setStudentId(studentId);
        report.setDescription(request.getDescription());
        report.setDeviceType(request.getDeviceType());
        report.setStatus("未完成");
        LocalDateTime now = LocalDateTime.now();

        //当前时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = now.format(formatter);
        report.setTime(currentTime);

        report.setTime(currentTime);
        int r = reportMapper.insert(report);
        if (r > 0) {
            log.info("学生 {} 创建报修单成功，单号：{}", studentId, report.getId());
        }
        else {
            throw new IllegalArgumentException("创建报修单失败");
        }
    }

    @Override
    //查看报修单
    public List<report> getReports(Long studentId) {
        QueryWrapper<report> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("studentId", studentId);
        return reportMapper.selectList(queryWrapper);
    }

    @Override
    //取消报修单
    public void cancelReport(Long studentId, CancelReportRequest request) {
        report report = reportMapper.selectById(request.getReportId());
        if (report == null) {
            throw new IllegalArgumentException("报修单不存在。");
        }
        if (!report.getStudentId().equals(studentId)) {
            throw new IllegalArgumentException("您不能取消他人的报修单");
        }
        if ("已完成".equals(report.getStatus())) {
            throw new IllegalArgumentException("您不能取消已完成的报修单");
        }
        int r = reportMapper.deleteById(report);
        if (r > 0) {
            log.info("学生 {} 取消报修单成功，单号：{}", studentId, request.getReportId());
        }
        else {
            throw new IllegalArgumentException("取消报修单失败！");
        }
    }

    @Override
    //修改密码
    public void changePassword(Long studentId, ChangePasswordRequest request) {
        Student student = getStudentById(studentId);
        if (!student.getPassword().equals(request.getOldPassword())) {
            throw new IllegalArgumentException("旧密码错误！");
        }

        // 新密码加密存储
        student.setPassword(passwordEncoder.encode(request.getNewPassword()));
        int r = studentMapper.updateById(student);
        if (r == 0) {
            throw new IllegalArgumentException("修改密码失败");
        }
        log.info("学生 {} 修改密码成功", studentId);
    }

    @Override
    //对获取学生进行封装
    public Student getStudentById(Long id) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new IllegalArgumentException("学生不存在");
        }
        return student;
    }
}

