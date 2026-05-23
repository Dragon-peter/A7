package com.example.A7.Security;

import com.example.A7.Config.JwtConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtConfig jwtConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 跳过公开路径的JWT验证
        String requestPath = request.getRequestURI();
        if (isPublicPath(requestPath)) {
            chain.doFilter(request, response);
            return;
        }

        final String requestTokenHeader = request.getHeader(jwtConfig.getHeader());

        String username = null;
        String jwtToken = null;

        // 从Authorization头部获取JWT令牌
        if (requestTokenHeader != null && requestTokenHeader.startsWith(jwtConfig.getTokenPrefix() + " ")) {
            jwtToken = requestTokenHeader.substring(jwtConfig.getTokenPrefix().length() + 1);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (Exception e) {
                logger.error("无法解析JWT令牌", e);
            }
        } else {
            // 尝试从query参数获取token（用于SSE等不支持自定义headers的请求）
            String tokenParam = request.getParameter("token");
            if (tokenParam != null && !tokenParam.trim().isEmpty()) {
                jwtToken = tokenParam;
                try {
                    username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                    logger.debug("从query参数获取到JWT令牌");
                } catch (Exception e) {
                    logger.error("无法解析query参数中的JWT令牌", e);
                }
            } else {
                logger.warn("JWT令牌不以Bearer开头或不存在，且query参数中也没有token");
            }
        }

        // 验证令牌并设置认证
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * 检查是否为公开路径，无需JWT验证
     */
    private boolean isPublicPath(String requestPath) {
        // 公开的路径列表
        String[] publicPaths = {
            "/api/auth/",
            "/api/user/register",
            "/api/ai/",
            "/api/teaching-ai/",
            "/api/file/",
            "/api/course/",
            "/api/knowledge-point/",
            "/api/local/"
        };

        for (String publicPath : publicPaths) {
            if (requestPath.startsWith(publicPath) || requestPath.equals(publicPath.substring(0, publicPath.length() - 1))) {
                return true;
            }
        }
        return false;
    }
}