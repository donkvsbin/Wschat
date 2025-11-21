package cn.cdut.ws.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置
 * 禁用默认登录页面，允许所有请求访问
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // 禁用 CSRF（WebSocket 需要）
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // 允许所有请求
            )
            .formLogin(form -> form.disable()) // 禁用表单登录
            .httpBasic(basic -> basic.disable()); // 禁用 HTTP Basic 认证

        return http.build();
    }
}
