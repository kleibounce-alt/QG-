package org.example.dormitory.service;

import org.example.dormitory.entity.ReportImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author klei
 */
public interface ReportImageService {
    //上传图片
    String uploadImage(Long reportId, Long studentId, MultipartFile file) throws Exception;
    //查看图片
    List<ReportImage> getImagesByReportId(Long reportId, Long currentUserId, String role);
}
