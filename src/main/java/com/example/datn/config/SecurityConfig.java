package com.example.datn.config;

import com.example.datn.service.CustomAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/ban-hang-tai-quay/**", "/cart/**", "admin/**","/hoa-don/**")
                )
                .authorizeHttpRequests((requests) -> requests
                                .requestMatchers("/", "/login", "/logout", "/forgot-password", "/reset-password", "/error", "/admin/product-catalog", "/register").permitAll()
//                        .requestMatchers("/admin/product-catalog").hasAnyAuthority("ROLE_KHACHHANG")
                                .requestMatchers("/thong-ke/index", "/admin/nhan-vien/**").hasAnyAuthority("Quản lý")
                                .requestMatchers("/admin/khach-hang/**", "/trang-ca-nhan/index", "/ban-hang-tai-quay/ban-hang", "/hoa-don/**", "/admin/phieu-giam/**").hasAnyAuthority("Nhân viên bán hàng", "Quản lý")
                                .requestMatchers(HttpMethod.POST, "/ban-hang-tai-quay/**","/admin/san-pham/**", "/cart/**").permitAll()  // Cho phép POST
                                .requestMatchers(HttpMethod.PUT, "/ban-hang-tai-quay/**","/admin/san-pham/**", "/cart/**").permitAll()   // Cho phép PUT
                                .requestMatchers(HttpMethod.DELETE, "/ban-hang-tai-quay/**","/admin/san-pham/**", "/cart/**").permitAll()// Cho phép DELETE

                                .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .successHandler(customAuthenticationSuccessHandler)
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .exceptionHandling((exceptions) -> exceptions
                        .accessDeniedPage("/403") // Trang lỗi khi truy cập bị từ chối
                );

        return http.build();
    }
}
