package com.school.homework.config;

import com.school.homework.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // 开启全局CORS配置
                .csrf(AbstractHttpConfigurer::disable) // 关闭 CSRF 防护
                .sessionManagement(session -> // 配置 Session 管理策略,设置无状态
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/users", "/api/users/login").permitAll()
                        // 显式放行 /api/chat 及其所有子路径，并放行 /error 路径以查看真实报错
                        .requestMatchers("/api/chat", "/api/chat/**", "/error").permitAll() 
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable) // 关闭 Spring Security 默认登录页
                .httpBasic(AbstractHttpConfigurer::disable); // 关闭 httpBasic 默认弹窗认证

        return http.build();
    }
}
