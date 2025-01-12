package com.example.datn.repository.ThongKeRepo;

import com.example.datn.dto.Thongke.SanPhamBanChayDTO;
import com.example.datn.entity.HoaDon;
import com.example.datn.entity.HoaDonChiTiet;
import com.example.datn.entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ThongKeRepo extends JpaRepository<HoaDonChiTiet, Integer> {

    @Query("SELECT h FROM HoaDon h WHERE h.trangThai = 4 AND CAST(h.ngayTao AS date) = CAST(GETDATE() AS date)")
    List<HoaDon> timHoaDonHoanThanhHomNay();

    @Query("SELECT SUM(h.tongTienSauGiamGia) FROM HoaDon h WHERE h.trangThai = 4 AND CAST(h.ngayTao AS date) = CAST(GETDATE() AS date)")
    BigDecimal tinhTongTienThuDuocHomNay();

//    @Query("SELECT hdct.sanPhamChiTiet.sanPham.ten AS tenSanPham, SUM(hdct.soLuong) AS soLuong " +
//            "FROM HoaDonChiTiet hdct " +
//            "JOIN hdct.hoaDon hd " +
//            "WHERE (:tuNgay IS NULL OR hd.ngayTao >= :tuNgay) " +
//            "AND (:denNgay IS NULL OR hd.ngayTao <= :denNgay) " +
//            "GROUP BY hdct.sanPhamChiTiet.sanPham.ten " +
//            "ORDER BY SUM(hdct.soLuong) DESC")
    @Query("SELECT hdct.sanPhamChiTiet.sanPham.ten AS tenSanPham, SUM(hdct.soLuong) AS soLuong " +
            "FROM HoaDonChiTiet hdct " +
            "JOIN hdct.hoaDon hd " +
            "WHERE hd.trangThai = 4 " +
            "AND (:tuNgay IS NULL OR hd.ngayTao >= :tuNgay) " +
            "AND (:denNgay IS NULL OR hd.ngayTao <= :denNgay) " +
            "GROUP BY hdct.sanPhamChiTiet.sanPham.ten " +
            "ORDER BY SUM(hdct.soLuong) DESC")
    List<Object[]> timSanPhamBanChay(LocalDate tuNgay, LocalDate denNgay);

//    @Query("SELECT hd.id AS hoaDonId, SUM(hdct.soLuong * hdct.sanPhamChiTiet.gia) AS tongDoanhThu " +
//            "FROM HoaDonChiTiet hdct " +
//            "JOIN hdct.hoaDon hd " +
//            "WHERE (:tuNgay IS NULL OR hd.ngayTao >= :tuNgay) " +
//            "AND (:denNgay IS NULL OR hd.ngayTao <= :denNgay) " +
//            "GROUP BY hd.id " +
//            "ORDER BY tongDoanhThu DESC")
    @Query("SELECT hd.id AS hoaDonId, SUM(hdct.soLuong * hdct.sanPhamChiTiet.gia) AS tongDoanhThu " +
            "FROM HoaDonChiTiet hdct " +
            "JOIN hdct.hoaDon hd " +
            "WHERE hd.trangThai = 4 " +
            "AND (:tuNgay IS NULL OR hd.ngayTao >= :tuNgay) " +
            "AND (:denNgay IS NULL OR hd.ngayTao <= :denNgay) " +
            "GROUP BY hd.id " +
            "ORDER BY tongDoanhThu DESC")
    List<Object[]> timHoaDonTheoDoanhThu(LocalDate tuNgay, LocalDate denNgay);

//    @Query("SELECT new com.example.datn.dto.Thongke.SanPhamBanChayDTO(hdct.sanPhamChiTiet, SUM(hdct.soLuong)) " +
//            "FROM HoaDonChiTiet hdct " +
//            "JOIN hdct.hoaDon hd " +
//            "WHERE (:tuNgay IS NULL OR hd.ngayTao >= :tuNgay) " +
//            "AND (:denNgay IS NULL OR hd.ngayTao <= :denNgay) " +
//            "GROUP BY hdct.sanPhamChiTiet " +
//            "ORDER BY SUM(hdct.soLuong) DESC")
//    List<SanPhamBanChayDTO> timSanPhamBanChay(LocalDate tuNgay, LocalDate denNgay);

}
