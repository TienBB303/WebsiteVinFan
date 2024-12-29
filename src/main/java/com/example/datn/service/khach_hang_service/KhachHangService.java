package com.example.datn.service.khach_hang_service;

import com.example.datn.entity.KhachHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface KhachHangService {
    Page<KhachHang>search(String keyword, Boolean trang_thai, Pageable pageable);
}
