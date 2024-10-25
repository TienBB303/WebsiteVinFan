package com.example.datn.repository;

import com.example.datn.dto.response.HoaDonResponse;
import com.example.datn.entity.HoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HoaDonRepo extends JpaRepository<HoaDon, Integer> {
    @Query("select hd from HoaDon hd order by hd.ngayTao desc ")
    Page<HoaDon> findHoaDonAndSortDay(Pageable pageable);

    @Query("SELECT hd FROM HoaDon hd " +
            "JOIN hd.khachHang kh " +
            "join hd.nhanVien nv " +
            "WHERE (LOWER(kh.ten) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(kh.ma) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(kh.soDienThoai) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(nv.ma) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(hd.ma) LIKE LOWER(CONCAT('%', :query, '%')))"
    )
    Page<HoaDon> searchHoaDon(String query, Pageable pageable);

    @Query("SELECT new com.example.datn.dto.response.HoaDonResponse(" +
            "pgg.ma , pgg.ten, pgg.giaTriMax, hd.tongTien, hd.tongTienSauGiamGia, hd.phiVanChuyen) " +
            "FROM HoaDon hd " +
            "join hd.phieuGiamGia pgg " +
            "where hd.id =:hoaDonId")
    HoaDonResponse findPGGByHoaDonId(@Param("hoaDonId") int hoaDonId);

    Page<HoaDon> findAllByTrangThai(Integer trangThai, Pageable pageable);


}
