package com.example.datn.service.thuoc_tinh_service;

import com.example.datn.entity.thuoc_tinh.CheDoGio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CheDoGioService {
    Page<CheDoGio> search(String query, Boolean trang_thai, Pageable pageable);
}
