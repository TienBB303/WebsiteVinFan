package com.example.datn.config;

import com.example.datn.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/ban-hang-tai-quay/**") // Chỉ vô hiệu hóa CSRF cho bán hàng
                )
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/", "/login", "/error","/admin/website/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/ban-hang-tai-quay/**").permitAll()  // Cho phép POST
                        .requestMatchers(HttpMethod.PUT, "/ban-hang-tai-quay/**").permitAll()   // Cho phép PUT
                        .requestMatchers(HttpMethod.DELETE, "/ban-hang-tai-quay/**").permitAll()// Cho phép DELETE
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/admin/index", true)
                        .permitAll()
                )
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll();

        return http.build();
    }
}