package com.example.datn.service;

import com.example.datn.entity.SanPham;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.entity.thuoc_tinh.CongSuat;
import com.example.datn.entity.thuoc_tinh.MauSac;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface SanPhamService {

    Page<SanPhamChiTiet> findAll(Pageable pageable);

    List<SanPhamChiTiet>  findAll();

    void create(SanPham sanPham, List<SanPhamChiTiet> sanPhamChiTietList);

    SanPhamChiTiet findById(Long id);

    Boolean update(SanPhamChiTiet sanPhamChiTiet);

    Boolean update(SanPham sanPham);

    Boolean delete(Long id);

    String taoMaTuDong();

    Page<SanPhamChiTiet> searchProducts(String query, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    BigDecimal getSanPhamGiaLonNhat();

    List<SanPhamChiTiet> timSanPhamTheoTen(String ten);

    Boolean tatCaSanPhamTrangThaiOff(Long sanPhamId);

    Boolean motSanPhamTrangThaiOn(Long sanPhamId);

    Boolean checkTrungLap(String ten, CongSuat congSuat, MauSac mauSac);

    Boolean checkHetSoLuong(Long sanPhamId);

    SanPhamChiTiet getProductDetails(Long productId);

    Page<SanPham> findByMaOrTen(String ten, Pageable pageable);

}
