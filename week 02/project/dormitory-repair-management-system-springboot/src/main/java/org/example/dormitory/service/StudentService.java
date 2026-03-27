package org.example.dormitory.service;

import org.example.dormitory.dto.*;
import org.example.dormitory.entity.Student;
import org.example.dormitory.entity.report;

import java.util.List;

/**
 * @author klei
 */
public interface StudentService {
    // 注册
    void register(RegisterRequest request);
    // 登录，返回 token
    String login(LoginRequest request);
    // 绑定/修改宿舍
    void bindDormitory(Long studentId, DormitoryBindingRequest request);
    // 创建报修单
    void createReport(Long studentId, CreateReportRequest request);
    // 查看报修记录
    List<report> getReports(Long studentId);
    // 取消报修单
    void cancelReport(Long studentId, CancelReportRequest request);
    // 修改密码
    void changePassword(Long studentId, ChangePasswordRequest request);
    // 获取当前学生信息（封装）
    Student getStudentById(Long id);
}
