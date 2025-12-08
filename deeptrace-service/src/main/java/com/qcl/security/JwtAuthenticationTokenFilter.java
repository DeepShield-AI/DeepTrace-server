package com.qcl.security;

import com.alibaba.druid.util.StringUtils;
import com.qcl.entity.User;
import com.qcl.service.UserService;
import com.qcl.utils.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


/**
 * 拦截所有进入系统的 HTTP 请求，从请求头中提取 JWT Token
 * JWT登录授权过滤器
 *
 * 请求示例
 * GET /api/user/profile HTTP/1.1
 * Host: localhost:8080
 * Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
 * Content-Type: application/json
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);
    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
//        try {

        String authHeader = request.getHeader(this.tokenHeader);
        if (authHeader != null && authHeader.startsWith(this.tokenHead)) {
            String authToken = authHeader.substring(this.tokenHead.length());// The part after "Bearer "
            String username = jwtTokenUtil.getUserNameFromToken(authToken);
            LOGGER.info("checking username:{}", username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = this.userService.queryByUsername(username);
                if (user !=null  && jwtTokenUtil.validateToken(authToken, user.getUsername())) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    LOGGER.info("authenticated user:{}", username);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }/*else {
                throw new RuntimeException("暂未登录或token已经过期");
            }*/
        }/*else {
            throw new RuntimeException("请传入认证token");
        }*/
//        } catch (Exception e) {
//            // 设置响应头
//            response.setContentType("application/json;charset=UTF-8");
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//
//            // 构造错误响应
//            String jsonResponse = "{\"code\":401,\"message\":\"" + e.getMessage() + "\",\"data\":null}";
//            response.getWriter().write(jsonResponse);
//            return; // 直接返回，不再继续执行过滤链
//        }
        chain.doFilter(request, response);
    }


}
