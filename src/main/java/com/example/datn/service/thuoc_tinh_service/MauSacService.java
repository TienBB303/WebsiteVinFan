package com.example.datn.service.thuoc_tinh_service;

import com.example.datn.entity.thuoc_tinh.MauSac;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MauSacService {
    Page<MauSac> search(String query, Boolean trang_thai, Pageable pageable);
}
