package com.example.datn.service.phieu_giam_service;

import com.example.datn.entity.SanPham;
import com.example.datn.entity.SanPhamChiTiet;

public interface PhieuGiamService {
     String taoMaTuDong();

     SanPham findById(Long id);

     SanPhamChiTiet findDetailById(Long id);
}
