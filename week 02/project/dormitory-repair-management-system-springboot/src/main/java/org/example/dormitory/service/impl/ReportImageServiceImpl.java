package org.example.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.dormitory.entity.report;
import org.example.dormitory.entity.ReportImage;
import org.example.dormitory.mapper.ReportImageMapper;
import org.example.dormitory.mapper.reportMapper;
import org.example.dormitory.service.ReportImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ReportImageServiceImpl implements ReportImageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private ReportImageMapper reportImageMapper;

    @Autowired
    private reportMapper reportMapper;

    /**
     * 获取实际存储目录（基于项目根目录的绝对路径）
     */
    private Path getUploadPath() {
        String userDir = System.getProperty("user.dir");
        Path path = Paths.get(userDir).resolve(uploadDir).normalize();
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("无法创建上传目录: " + path, e);
        }
        return path;
    }

    @Override
    public String uploadImage(Long reportId, Long studentId, MultipartFile file) throws IOException {
        // 1. 校验报修单是否存在且属于当前学生
        report report = reportMapper.selectById(reportId);
        if (report == null || !report.getStudentId().equals(studentId)) {
            throw new RuntimeException("报修单不存在或无权操作");
        }

        // 2. 校验文件类型
        String originalName = file.getOriginalFilename();
        String ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        if (!ext.equals(".jpg") && !ext.equals(".png")) {
            throw new RuntimeException("只支持jpg/png格式");
        }

        // 3. 生成新文件名，防止重名
        String newFileName = UUID.randomUUID().toString() + ext;
        Path uploadPath = getUploadPath();
        Path filePath = uploadPath.resolve(newFileName);
        file.transferTo(filePath.toFile());

        // 4. 保存相对路径到数据库（前端访问时通过 /uploads/ 映射）
        String relativePath = "/uploads/report_images/" + newFileName;
        ReportImage reportImage = new ReportImage();
        reportImage.setReportId(reportId);
        reportImage.setImagePath(relativePath);
        reportImage.setOriginalName(originalName);
        reportImage.setCreateTime(System.currentTimeMillis());
        reportImageMapper.insert(reportImage);

        return relativePath;
    }

    @Override
    public List<ReportImage> getImagesByReportId(Long reportId, Long currentUserId, String role) {
        // 权限校验：学生只能看自己的报修单图片，管理员可以看所有
        if ("ROLE_STUDENT".equals(role)) {
            report report = reportMapper.selectById(reportId);
            if (report == null || !report.getStudentId().equals(currentUserId)) {
                throw new RuntimeException("无权查看该报修单的图片");
            }
        }
        // 管理员无需额外校验
        LambdaQueryWrapper<ReportImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportImage::getReportId, reportId);
        return reportImageMapper.selectList(wrapper);
    }
}