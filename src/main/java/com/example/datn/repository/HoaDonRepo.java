package com.example.datn.repository;

import com.example.datn.dto.response.LichSuThanhToanResponse;
import com.example.datn.dto.response.PggInHoaDonResponse;
import com.example.datn.entity.HoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface HoaDonRepo extends JpaRepository<HoaDon, Long> {
    @Query("select hd from HoaDon hd order by hd.ngayTao desc ")
    Page<HoaDon> findHoaDonAndSortDay(Pageable pageable);

    @Query("SELECT hd FROM HoaDon hd " +
            "LEFT JOIN hd.khachHang kh " +
            "LEFT JOIN hd.nhanVien nv " +
            "WHERE (LOWER(COALESCE(kh.ten, '')) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(COALESCE(kh.ma, '')) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(COALESCE(kh.soDienThoai, '')) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(COALESCE(nv.ma, '')) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(COALESCE(hd.ma, '')) LIKE LOWER(CONCAT('%', :query, '%')))"
    )
    Page<HoaDon> searchHoaDon(String query, Pageable pageable);


    @Query("SELECT new com.example.datn.dto.response.PggInHoaDonResponse(" +
            "pgg.ma , pgg.ten, hd.tongTien, hd.tongTienSauGiamGia, hd.phiVanChuyen) " +
            "FROM HoaDon hd " +
            "join hd.phieuGiamGia pgg " +
            "where hd.id =:hoaDonId")
    PggInHoaDonResponse findPGGByHoaDonId(@Param("hoaDonId") long hoaDonId);

    @Query("SELECT new com.example.datn.dto.response.LichSuThanhToanResponse(" +
            "hd.tongTien , hd.ngayTao, hd.loaiHoaDon,httt.hinhThucThanhToan, hd.trangThai, hd.nguoiTao) " +
            "FROM HoaDon hd " +
            "join hd.hinhThucThanhToan httt " +
            "where hd.id =:hoaDonId")
    LichSuThanhToanResponse findThanhToanHoaDonId(@Param("hoaDonId") long hoaDonId);

    Page<HoaDon> findAllByTrangThai(Integer trangThai, Pageable pageable);

    @Modifying
    @Query("UPDATE HoaDon h SET h.tongTien = (SELECT SUM(hcd.thanhTien) FROM HoaDonChiTiet hcd WHERE hcd.hoaDon.id = h.id)")
    void updateTongTienHoaDon();



    @Query("SELECT hd FROM HoaDon hd WHERE CAST(hd.ngayTao AS DATE) = :date ORDER BY hd.ngayTao DESC")
    Page<HoaDon> findByNgayTao(@Param("date") LocalDate date, Pageable pageable);

    //khoi
    @Query("SELECT hd FROM HoaDon hd WHERE hd.loaiHoaDon = :isOnline")
    Page<HoaDon> findAllByLoaiHoaDon(@Param("isOnline") boolean isOnline, Pageable pageable);

    @Query("SELECT hd FROM HoaDon hd WHERE hd.ngayTao BETWEEN :startDate AND :endDate ORDER BY hd.ngayTao DESC")
    Page<HoaDon> findHoaDonByDateRange(@Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate,
                                       Pageable pageable);

}
