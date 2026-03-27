package org.example.dormitory.service;

import org.example.dormitory.dto.*;
import org.example.dormitory.entity.Admin;
import org.example.dormitory.entity.report;

import java.util.List;

/**
 * @author klei
 */
public interface AdminService {
    //注册
    void register(RegisterRequest registerRequest);
    //登录
    String login(LoginRequest request);
    //查看所有报修单
    List<report> getAllReports();
    //查看某个报修单
    report getOneReport(GetReportRequest getReportRequest);
    //更新报修单
    void updateReport(UpdateReportRequest updateReportRequest);
    //删除报修单
    void deleteReport(CancelReportRequest cancelReportRequest);
    //修改密码
    void changePassword(Long id, ChangePasswordRequest changePasswordRequest);
    //获取管理员信息(封装)
    Admin getAdminById(Long id);
}
