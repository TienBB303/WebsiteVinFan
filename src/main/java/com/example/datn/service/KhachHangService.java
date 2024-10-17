package com.example.datn.service;


import com.example.datn.entity.KhachHang;
import com.example.datn.repository.KhachHangRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KhachHangService {

    @Autowired
    private KhachHangRepo khachHangRepo;

    public void updateTrangThai(Integer id, Boolean trangThai) {
        KhachHang khachHang = khachHangRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));

        khachHang.setTrangThai(trangThai);
        khachHangRepo.save(khachHang);
    }
}