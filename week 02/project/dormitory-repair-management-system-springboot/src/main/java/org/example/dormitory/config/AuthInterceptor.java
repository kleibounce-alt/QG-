package org.example.dormitory.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dormitory.common.Result;
import org.example.dormitory.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    // 放行路径
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/student/login",
            "/api/student/register",
            "/api/admin/login",
            "/api/admin/register"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // 1. 放行公开接口
        if (PUBLIC_PATHS.contains(uri)) {
            return true;
        }

        // 2. 获取并验证 Token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(response, "缺少认证信息");
            return false;
        }

        String token = authHeader.substring(7);
        if (!jwtUtils.validateToken(token)) {
            sendUnauthorized(response, "无效的 token");
            return false;
        }

        // 3. 解析 Token 获取用户信息
        Claims claims = jwtUtils.getClaims(token);
        String userIdStr = claims.getSubject();
        String role = claims.get("role", String.class);
        if (role == null) {
            sendUnauthorized(response, "无效的 token 角色");
            return false;
        }

        // 4. 权限校验：根据路径前缀判断角色是否有权访问
        if (uri.startsWith("/api/student/") && !"ROLE_STUDENT".equals(role)) {
            sendUnauthorized(response, "无权限访问学生资源");
            return false;
        }
        if (uri.startsWith("/api/admin/") && !"ROLE_ADMIN".equals(role)) {
            sendUnauthorized(response, "无权限访问管理员资源");
            return false;
        }

        // 5. 将用户信息存入请求属性（供 Controller 使用）
        Long userId = Long.parseLong(userIdStr);
        request.setAttribute("userId", userId);
        request.setAttribute("role", role);
        if ("ROLE_STUDENT".equals(role)) {
            request.setAttribute("studentId", userId);
        }
        if ("ROLE_ADMIN".equals(role)) {
            request.setAttribute("adminId", userId);
        }

        return true;
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(Result.error(401, message)));
    }
}