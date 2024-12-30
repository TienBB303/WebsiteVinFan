package com.example.datn.service.Implements;

import com.example.datn.entity.KhachHang;
import com.example.datn.repository.KhachHangRepo;
import com.example.datn.service.khach_hang_service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class KhachHangImpl implements KhachHangService {
    @Autowired
    KhachHangRepo khachHangRepo;

    @Override
    public Page<KhachHang> search(String keyword, Boolean trang_thai, Pageable pageable) {
        if (trang_thai == null) {
            return khachHangRepo.searchKhachHangKhongCoTrangThai(keyword, pageable);
        }
        return khachHangRepo.searchKhachHang(keyword,trang_thai, pageable);
    }
}
