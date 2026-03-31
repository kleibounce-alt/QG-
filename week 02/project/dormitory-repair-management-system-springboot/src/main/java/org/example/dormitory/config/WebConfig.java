package org.example.dormitory.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/uploads/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ⭐ 关键修复：只映射到 uploads 目录，而不是 uploads/report_images
        // 因为请求路径是 /uploads/report_images/xxx.png
        // 去掉 /uploads/ 后剩下 report_images/xxx.png
        // 拼接到 uploads/ 正好是 uploads/report_images/xxx.png
        Path path = Paths.get(System.getProperty("user.dir"))
                .resolve("uploads")  // 注意：只到 uploads，不要加 report_images
                .normalize();

        String location = "file:" + path.toString().replace("\\", "/");
        if (!location.endsWith("/")) {
            location += "/";
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);

        System.out.println("====================================");
        System.out.println("静态资源映射: /uploads/** -> " + location);
        System.out.println("示例: 请求 /uploads/report_images/xxx.png");
        System.out.println("实际查找: " + location + "report_images/xxx.png");
        System.out.println("====================================");
    }
}