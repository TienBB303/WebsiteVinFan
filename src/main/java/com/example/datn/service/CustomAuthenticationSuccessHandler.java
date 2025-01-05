package com.example.datn.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Authentication authentication) throws IOException, jakarta.servlet.ServletException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        System.out.println("Authorities: " + roles);
        if (roles.contains("ROLE_KHACHHANG")) {
            response.sendRedirect("/vin-fan/danh-muc");
        } else if (roles.contains("Quản lý") || roles.contains("Nhân viên bán hàng")) {
            response.sendRedirect("/trang-ca-nhan/index");
        } else {
            response.sendRedirect("/login");
        }
    }
}
