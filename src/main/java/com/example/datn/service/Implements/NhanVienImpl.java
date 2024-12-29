package com.example.datn.service.Implements;

import com.example.datn.entity.NhanVien;
import com.example.datn.repository.NhanVienRepository;
import com.example.datn.service.nhan_vien_service.NhanVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class NhanVienImpl implements NhanVienService {
    @Autowired
    NhanVienRepository nhanVienRepository;

    @Override
    public Page<NhanVien> search(String keyword, Boolean trang_thai, Pageable pageable) {
        if (trang_thai == null) {
            return nhanVienRepository.searchNhanVienKhongCoTrangThai(keyword, pageable);
        }
        return nhanVienRepository.searchNhanVien(keyword,trang_thai, pageable);
    }
}
