package org.example.dormitory.config;

import io.jsonwebtoken.Claims;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dormitory.common.Result;
import org.example.dormitory.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/student/login",
            "/api/student/register",
            "/api/admin/login",
            "/api/admin/register"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String uri = request.getRequestURI();

        // 放行公开接口
        if (PUBLIC_PATHS.contains(uri)) {
            return true;
        }

        // 放行静态资源
        if (uri.startsWith("/uploads/")) {
            return true;
        }

        // 验证 Token
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

        // 解析 Token
        Claims claims = jwtUtils.getClaims(token);
        String userIdStr = claims.getSubject();
        String role = claims.get("role", String.class);

        if (role == null) {
            sendUnauthorized(response, "无效的 token 角色");
            return false;
        }

        Long userIdLong = Long.parseLong(userIdStr);

        // ⭐ 关键：将认证信息存入 Spring Security 上下文
        // 这样 @PreAuthorize 才能生效
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userIdLong,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority(role))
                );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 同时存入 request attribute（供 Controller 使用 @RequestAttribute）
        request.setAttribute("userId", userIdLong);
        request.setAttribute("role", role);
        if ("ROLE_STUDENT".equals(role)) {
            request.setAttribute("studentId", userIdLong);
        } else if ("ROLE_ADMIN".equals(role)) {
            request.setAttribute("adminId", userIdLong);
        }

        return true;
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(Result.error(401, message)));
    }
}