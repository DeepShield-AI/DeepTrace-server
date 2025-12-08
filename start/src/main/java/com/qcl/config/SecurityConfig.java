package com.qcl.config;

import com.alibaba.druid.util.StringUtils;
import com.qcl.security.JwtAuthenticationTokenFilter;
import com.qcl.security.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * 对于白名单中的 URL（如 /admin/login、/swagger-ui/ 等）：
 * 请求首先经过 JwtAuthenticationTokenFilter
 * 但即使没有 JWT token 或 token 无效，也不会阻止请求继续
 * 在 FilterSecurityInterceptor 中检查到该 URL 属于白名单
 * 直接允许访问，无需认证
 * 对于非白名单 URL：
 * 请求首先经过 JwtAuthenticationTokenFilter
 * 如果有有效 token，则设置认证信息
 * 在 FilterSecurityInterceptor 中检查访问权限
 * 如果没有认证信息或权限不足，则被拒绝访问
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Autowired
    private SecurityIgnoreUrlsConfig ignoreUrlsConfig;
    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(authorize -> {
                    //不需要保护的资源路径允许访问(比如登录、注册接口)
                    for (String url : ignoreUrlsConfig.getUrls()) {
                        authorize.requestMatchers(url).permitAll();
                    }
                    //允许跨域请求的OPTIONS请求
                    authorize.requestMatchers(HttpMethod.OPTIONS).permitAll();
                    //白名单外的任何请求都需要身份认证
                    authorize.anyRequest().authenticated();

                })
                //关闭跨站请求防护及不使用session
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //自定义权限拒绝处理类
                .exceptionHandling(exception -> {
                    //在这里添加自定义异常处理配置
                    exception.authenticationEntryPoint(restAuthenticationEntryPoint);

                })
                //自定义权限拦截器JWT过滤器
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

}
