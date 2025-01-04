package com.example.datn.service;

import com.example.datn.dto.request.AddSPToHoaDonChiTietRequest;
import com.example.datn.dto.response.*;
import com.example.datn.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface HoaDonService {
    Page<HoaDon> findAll(Pageable pageable);

    List<HoaDon> getAll();

    void save(HoaDon hoaDon);

    void delete(Long id);

    void saveHoaDonChiTiet(HoaDonChiTiet hoaDonChiTiet);

    Page<HoaDon> findHoaDonAndSortDay(int page, int size);

    List<ListSanPhamInHoaDonChiTietResponse> getSanPhamCTByHoaDonId(Long hoaDonId);

    List<HoaDonChiTiet> timSanPhamChiTietTheoHoaDon(Long idHoaDon);

    List<ListSpNewInHoaDonResponse> getSanPhamInHoaDon();

    PggInHoaDonResponse getPGGbyHoaDonId(Long hoaDonId);

    LichSuThanhToanResponse getLSTTByHoaDonId(Long hoaDonId);

    Optional<HoaDon> findById(Long id);

    Optional<SanPhamChiTiet> findByIdSanPhamChiTiet(Long id);

    Page<HoaDon> searchHoaDon(String query, Boolean loaiHoaDon, LocalDate tuNgay, LocalDate denNgay, Integer trang_thai, Pageable pageable);

    Page<HoaDon> getAllHoaDonByTrangThai(Integer trangThai, Pageable pageable);

    List<SanPhamChiTiet> getSPCTInHDCT();

    SanPhamChiTiet getIdSPCT(long idSPCT);

    void addSpToHoaDonChiTietRequestList(AddSPToHoaDonChiTietRequest request);

    HoaDonChiTiet convertToEntity(AddSPToHoaDonChiTietRequest dto, HoaDon hoaDon, SanPhamChiTiet sanPhamChiTiet);

    boolean xacNhanHoaDon(long id);

    boolean huyHoaDon(long id);

    boolean hoanThanhHoaDon(long id);

    void updateTongTienHoaDon(Long idHoaDon);

    Optional<HoaDon> findByIdWithPhieuGiamGia(Long id);

    void deleteSPInHD(Long idSanPhamChiTiet);

    HinhThucThanhToanResponse getHinhThucThanhToan();

    void tangSoLuongSanPham(Long idHoaDon, Long idSanPhamChiTiet);

    void giamSoLuongSanPham(Long idHoaDon, Long idSanPhamChiTiet);

    void truSoLuongSanPham(Long idHD);


    List<HoaDon> getHoaDonByIdKH(Integer idKH);

    String generateOrderCode();

    Page<HoaDon> getAllHoaDonByLoaiHoaDon(boolean isOnline, Pageable pageable);

    Page<HoaDon> getHoaDonByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);

//    TienBB
    Map<Integer, Long> countHoaDonByTrangThai();
}
