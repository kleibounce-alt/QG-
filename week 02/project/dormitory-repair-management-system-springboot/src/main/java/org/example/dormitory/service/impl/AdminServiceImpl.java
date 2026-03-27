package org.example.dormitory.service.impl;

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
    //注册
    public void register(RegisterRequest request) {
        String id = String.valueOf(request.getId());
        if (!id.matches("^(0025)\\d{6}$")) {
            throw new IllegalArgumentException("学号前缀必须为0025且长度为10位");
        }
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        long count = adminMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new IllegalArgumentException("该工号已存在");
        }

        Admin admin = new Admin();
        admin.setId(request.getId());
        //密码加密
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        adminMapper.insert(admin);
        log.info("管理员注册成功：{}", request.getId());
    }

    @Override
    //登录
    public String login(LoginRequest request) {
        Admin admin = getAdminById(request.getId());
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("工号或密码错误");
        }

        //生成JWT
        String token = jwtUtils.generateToken(String.valueOf(admin.getId()), "ROLE_ADMIN");
        log.info("管理员登录成功：{}", admin.getId());
        return token;
    }

    @Override
    //查看所有报修表
    public List<report> getAllReports () {
        return reportMapper.selectList(null);
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
    //修改密码
    public void changePassword(Long id, ChangePasswordRequest request) {
        Admin admin = getAdminById(id);
        if (!passwordEncoder.matches(request.getOldPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("旧密码错误！");
        }

        //加密储存
        admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
        int r = adminMapper.updateById(admin);
        if (r > 0) {
            log.info("修改成功：{}", admin.getId());
        }
        else {
            throw new IllegalArgumentException("修改失败！");
        }
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
