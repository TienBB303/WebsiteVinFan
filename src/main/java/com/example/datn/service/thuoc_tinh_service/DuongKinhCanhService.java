package com.example.datn.service.thuoc_tinh_service;

import com.example.datn.entity.thuoc_tinh.DuongKinhCanh;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DuongKinhCanhService {
    Page<DuongKinhCanh> search(String query, Boolean trang_thai, Pageable pageable);
}
