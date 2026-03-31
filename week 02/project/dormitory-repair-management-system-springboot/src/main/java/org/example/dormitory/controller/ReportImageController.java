package org.example.dormitory.controller;

import org.example.dormitory.common.Result;
import org.example.dormitory.entity.ReportImage;
import org.example.dormitory.service.ReportImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ReportImageController {

    @Autowired
    private ReportImageService reportImageService;

    @PostMapping("/upload")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<String> upload(@RequestAttribute("studentId") Long studentId,
                                 @RequestParam("reportId") Long reportId,
                                 @RequestParam("file") MultipartFile file) {
        try {
            String path = reportImageService.uploadImage(reportId, studentId, file);
            return Result.success(path);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/report/{reportId}")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<List<ReportImage>> getImagesByStudent(@RequestAttribute("studentId") Long studentId,
                                                        @PathVariable Long reportId) {
        List<ReportImage> images = reportImageService.getImagesByReportId(reportId, studentId, "ROLE_STUDENT");
        return Result.success(images);
    }

    @GetMapping("/admin/report/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<ReportImage>> getImagesByAdmin(@RequestAttribute("adminId") Long adminId,
                                                      @PathVariable Long reportId) {
        List<ReportImage> images = reportImageService.getImagesByReportId(reportId, null, "ROLE_ADMIN");
        return Result.success(images);
    }
}