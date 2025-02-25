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

    List<SanPhamChiTiet> findByIdSanPham(Long id);

    Boolean update(SanPhamChiTiet sanPhamChiTiet);

    Boolean update(SanPham sanPham);

    Boolean delete(Long id);

    String taoMaTuDong();

    Page<SanPhamChiTiet> searchProducts(Long idSanPhamString, String query, BigDecimal minPrice, BigDecimal maxPrice, Boolean trang_thai, Pageable pageable);

    List<SanPhamChiTiet> xuatExcel(String query, BigDecimal minPrice, BigDecimal maxPrice, Boolean trang_thai);

    BigDecimal getSanPhamGiaLonNhat();

    BigDecimal getSanPhamGiaNhoNhat();

    List<SanPhamChiTiet> timSanPhamTheoTen(String ten);

    Boolean tatCaSanPhamTrangThaiOff(Long sanPhamId);

    Boolean motSanPhamTrangThaiOn(Long sanPhamId);

    Boolean checkTrungLap(String ten, CongSuat congSuat, MauSac mauSac);

    Boolean checkHetSoLuong(Long sanPhamId);

    SanPhamChiTiet getProductDetails(Long productId);

    Page<SanPham> findByMaOrTen(String ten, Pageable pageable);

//    List danh sach san pham

    Page<SanPham> timSanPham(String query, Boolean trang_thai, Pageable pageable);

}
