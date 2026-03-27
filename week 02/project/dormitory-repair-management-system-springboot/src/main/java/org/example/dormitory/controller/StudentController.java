package org.example.dormitory.controller;

import org.example.dormitory.common.Result;
import org.example.dormitory.dto.*;
import org.example.dormitory.entity.Student;
import org.example.dormitory.entity.report;
import org.example.dormitory.mapper.StudentMapper;
import org.example.dormitory.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author klei
 */
@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterRequest request) {
        studentService.register(request);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginRequest request) {
        String token = studentService.login(request);
        return Result.success(token);
    }

    @PutMapping("/dormitory")
    public Result<Void> bindDormitory(@RequestAttribute("studentId") Long studentId, @RequestBody DormitoryBindingRequest request) {
        studentService.bindDormitory(studentId, request);
        return Result.success();
    }

    @PostMapping("/reports")
    public Result<Void> createReport(@RequestAttribute("studentId")  Long studentId, @RequestBody CreateReportRequest request) {
        studentService.createReport(studentId, request);
        return Result.success();
    }

    @GetMapping("/reports")
    public Result<List<report>> getReports(@RequestAttribute("studentId") Long studentId) {
        List<report> reports = studentService.getReports(studentId);
        return Result.success(reports);
    }

    @DeleteMapping("/reports/{reportId}")
    public Result<Void> cancelReport(@RequestAttribute("studentId") Long studentId, @PathVariable Integer reportId) {
        CancelReportRequest request = new CancelReportRequest();
        request.setReportId(reportId);
        studentService.cancelReport(studentId, request);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@RequestAttribute("studentId") Long studentId, @RequestBody ChangePasswordRequest request) {
        studentService.changePassword(studentId, request);
        return Result.success();
    }

}
