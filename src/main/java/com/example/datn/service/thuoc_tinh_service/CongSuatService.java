package com.example.datn.service.thuoc_tinh_service;

import com.example.datn.entity.thuoc_tinh.CongSuat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CongSuatService {
    Page<CongSuat> search(String query, Boolean trang_thai, Pageable pageable);
}
