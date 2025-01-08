//package com.example.datn.repository.ThongKeRepo;
//
//import com.example.datn.dto.response.ThongKeResponse;
//import com.example.datn.dto.response.ThongKeSanPhamResponse;
//import com.example.datn.entity.HoaDonChiTiet;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Repository
//public interface ThongKeRepo extends JpaRepository<HoaDonChiTiet, Integer> {
//    @Query("SELECT new com.example.datn.dto.response.ThongKeResponse(" +
//            "'YEAR'," +
//            "EXTRACT(YEAR FROM hd.ngayTao), " +
//            "sum(hdct.thanhTien), " +
//            "sum(hdct.soLuong))" +
//            "from HoaDonChiTiet hdct " +
//            "join hdct.hoaDon hd " +
//            "join hdct.sanPhamChiTiet spct " +
//            " where hd.trangThai = 4 " +
//            " group by EXTRACT(YEAR FROM hd.ngayTao)")
//    List<ThongKeResponse> getListYear();
//
//    @Query("SELECT new com.example.datn.dto.response.ThongKeResponse(" +
//            "'MONTH'," +
//            "EXTRACT(MONTH FROM hd.ngayTao), " +
//            "sum(hdct.thanhTien), " +
//            "sum(hdct.soLuong))" +
//            "from HoaDonChiTiet hdct " +
//            "join hdct.hoaDon hd " +
//            "join hdct.sanPhamChiTiet spct " +
//            " where hd.trangThai = 4 " +
//            " group by EXTRACT(MONTH FROM hd.ngayTao)")
//    List<ThongKeResponse> getListMonth();
//
//    @Query("SELECT new com.example.datn.dto.response.ThongKeResponse(" +
//            "'WEEK'," +
//            "EXTRACT(week FROM hd.ngayTao), " +
//            "sum(hdct.thanhTien), " +
//            "sum(hdct.soLuong))" +
//            "from HoaDonChiTiet hdct" +
//            " join hdct.hoaDon hd " +
//            " join hdct.sanPhamChiTiet spct " +
//            " where hd.trangThai = 4 " +
//            " group by EXTRACT(week FROM hd.ngayTao)")
//    List<ThongKeResponse> getListWeek();
//
//    @Query("SELECT new com.example.datn.dto.response.ThongKeResponse(" +
//            "'DAY'," +
//            "EXTRACT(day FROM hd.ngayTao), " +
//            "sum(hdct.thanhTien), " +
//            "sum(hdct.soLuong)) " +
//            "from HoaDonChiTiet hdct " +
//            "join hdct.hoaDon hd " +
//            "join hdct.sanPhamChiTiet spct " +
//            "where hd.trangThai = 4 " +
//            "group by EXTRACT(day FROM hd.ngayTao)")
//    List<ThongKeResponse> getListDay();
//
//
//    @Query("SELECT new com.example.datn.dto.response.ThongKeSanPhamResponse(" +
//            "sp.ten, SUM(hdct.soLuong), SUM(hdct.thanhTien)) " +
//            "FROM HoaDonChiTiet hdct " +
//            "JOIN hdct.sanPhamChiTiet spct " +
//            "JOIN spct.sanPham sp " +
//            "join hdct.hoaDon hd " +
//            " where hd.trangThai = 4 " +
//            "GROUP BY sp.id, sp.ten " +
//            "ORDER BY SUM(hdct.soLuong) DESC")
//    List<ThongKeSanPhamResponse> findSanPhamBanChay();
//
////    TienBB
////    @Query("SELECT new com.example.datn.dto.response.ThongKeResponse(" +
////            "'DAY', " +
////            "EXTRACT(DAY FROM hd.ngayTao), " +
////            "sum(hdct.thanhTien), " +
////            "sum(hdct.soLuong)) " +
////            "FROM HoaDon hd " +
////            "JOIN HoaDonChiTiet hdct ON hd.id = hdct.hoaDon.id " +
////            "JOIN SanPhamChiTiet spct on hdct.sanPhamChiTiet.id = spct.id" +
////            "WHERE hd.ngayTao = CURRENT_DATE - 1 " +
////            "GROUP BY EXTRACT(DAY FROM hd.ngayTao)")
////    List<ThongKeResponse> getListYesterday();
//}
