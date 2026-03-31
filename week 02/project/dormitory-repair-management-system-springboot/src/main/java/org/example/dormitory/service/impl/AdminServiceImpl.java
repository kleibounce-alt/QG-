package org.example.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.dormitory.dto.*;
import org.example.dormitory.entity.Admin;
import org.example.dormitory.entity.report;
import org.example.dormitory.mapper.AdminMapper;
import org.example.dormitory.mapper.reportMapper;
import org.example.dormitory.service.AdminService;
import org.example.dormitory.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author klei
 */
@Service
@Slf4j
@Transactional
public class AdminServiceImpl implements AdminService{
    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private reportMapper reportMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void register(RegisterRequest request) {
        String idStr = request.getId();
        log.info("接收到的工号: {}", idStr);
        if (!idStr.matches("^0025\\d{6}$")) {
            throw new IllegalArgumentException("工号前缀必须为0025且长度为10位");
        }
        Long idLong = Long.valueOf(idStr);
        // 检查是否存在
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", idLong);
        long count = adminMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new IllegalArgumentException("该工号已存在");
        }
        Admin admin = new Admin();
        admin.setId(idLong);
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        adminMapper.insert(admin);
        log.info("管理员注册成功：{}", idStr);
    }

    @Override
    public String login(LoginRequest request) {
        String idStr = request.getId();
        Long idLong = Long.valueOf(idStr);
        Admin admin = getAdminById(idLong);
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("工号或密码错误");
        }
        String subject = String.format("%010d", admin.getId());
        String token = jwtUtils.generateToken(subject, "ROLE_ADMIN");
        log.info("管理员登录成功：{}", subject);
        return token;
    }

    @Override
    public List<report> getAllReports(String status) {
        LambdaQueryWrapper<report> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(report::getStatus, status);
        }
        return reportMapper.selectList(wrapper);
    }
    @Override
    //查看某一个报修表
    public report getOneReport(GetReportRequest request) {
        report report = reportMapper.selectById(request.getReportId());
        if (report == null) {
            throw new IllegalArgumentException("该报修单不存在");
        }
        return report;
    }

    @Override
    //更新报修表状态
    public void updateReport(UpdateReportRequest request) {
        report report = reportMapper.selectById(request.getReportId());
        if (report == null) {
            throw new IllegalArgumentException("该报修单不存在");
        }
        if (report.getStatus().equals("已完成")) {
            throw new IllegalArgumentException("该报修单已经是已完成状态");
        }
        report.setStatus("已完成");
        int r = reportMapper.updateById(report);
        if (r > 0) {
            log.info("该报修单状态更新完毕：{}", report.getId());
        }
        else {
            throw new IllegalArgumentException("更新失败");
        }
    }

    @Override
    //删除报修单
    public void deleteReport(CancelReportRequest request) {
        report report = reportMapper.selectById(request.getReportId());
        if (report == null) {
            throw new IllegalArgumentException("该报修单不存在");
        }
        int r = reportMapper.deleteById(report.getId());
        if (r > 0) {
            log.info("删除成功{}", report.getId());
        }
        else {
            throw new IllegalArgumentException("删除失败");
        }
    }

    @Override
    public void changePassword(Long id, ChangePasswordRequest request) {
        Admin admin = getAdminById(id);
        if (!passwordEncoder.matches(request.getOldPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("旧密码错误！");
        }
        admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
        int r = adminMapper.updateById(admin);
        if (r == 0) {
            throw new IllegalArgumentException("修改失败！");
        }
        log.info("管理员 {} 修改密码成功", id);
    }

    @Override
    //获取管理员
    public Admin getAdminById(Long id) {
        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            throw new IllegalArgumentException("管理员不存在");
        }
        return admin;
    }
}
