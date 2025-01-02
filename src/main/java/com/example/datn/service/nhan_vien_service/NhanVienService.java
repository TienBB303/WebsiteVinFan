package com.example.datn.service.nhan_vien_service;

import com.example.datn.entity.NhanVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NhanVienService {

    String taoMaTuDong();

    Page<NhanVien> search(String keyword, Boolean trang_thai, Pageable pageable);
}
