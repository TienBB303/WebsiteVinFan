package com.example.datn.service;

import com.example.datn.entity.KhachHang;
import com.example.datn.entity.NhanVien;
import com.example.datn.repository.KhachHangRepo;
import com.example.datn.repository.NhanVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
public class CustomUserDetailsService implements UserDetailsService {


    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private KhachHangRepo khachHangRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Kiểm tra xem email có thuộc về nhân viên không
        Optional<NhanVien> nhanVienOpt = nhanVienRepository.findByEmail(email);
        if (nhanVienOpt.isPresent()) {
            NhanVien nhanVien = nhanVienOpt.get();
            List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(nhanVien.getChucVu().getViTri()));
            return new User(nhanVien.getEmail(), nhanVien.getMatKhau(), authorities);
        }

        // Kiểm tra xem email có thuộc về khách hàng không
        Optional<KhachHang> khachHangOpt = khachHangRepository.findByEmail(email);
        if (khachHangOpt.isPresent()) {
            KhachHang khachHang = khachHangOpt.get();
            List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_KHACHHANG"));
            return new User(khachHang.getEmail(), khachHang.getMatKhau(), authorities);
        }

        throw new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email);
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

}
