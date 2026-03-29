package org.example.dormitory.controller;

import org.example.dormitory.common.Result;
import org.example.dormitory.dto.*;
import org.example.dormitory.entity.Admin;
import org.example.dormitory.entity.report;
import org.example.dormitory.mapper.AdminMapper;
import org.example.dormitory.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author klei
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterRequest request) {
        System.out.println("=== 进入 AdminController.register ===");
        System.out.println("接收到的工号: " + request.getId());
        adminService.register(request);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginRequest request) {
        String token = adminService.login(request);
        return Result.success(token);
    }

    @GetMapping("/reports")
    public Result<List<report>> getAllReports() {
        List<report> reports = adminService.getAllReports();
        return Result.success(reports);
    }

    @GetMapping("/reports/{reportId}")
    public Result<report> getOneReport (@PathVariable Integer reportId) {
        GetReportRequest request = new GetReportRequest();
        request.setReportId(reportId);
        report report = adminService.getOneReport(request);
        return Result.success(report);
    }

    @PutMapping("/reports/{reportId}")
    public Result<Void> updateReport(@PathVariable Integer reportId) {
        UpdateReportRequest request = new UpdateReportRequest();
        request.setReportId(reportId);
        adminService.updateReport(request);
        return Result.success();
    }


    @DeleteMapping("/reports/{reportId}")
    public Result<Void> deleteReport(@PathVariable Integer reportId) {
        CancelReportRequest request = new CancelReportRequest();
        request.setReportId(reportId);
        adminService.deleteReport(request);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@RequestAttribute("adminId") Long adminId, @RequestBody ChangePasswordRequest request ) {
        adminService.changePassword(adminId, request);
        return Result.success();
    }
}
