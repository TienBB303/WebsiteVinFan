package com.example.vinfan.repository;

import com.example.vinfan.dto.HoaDonChiTietDTO;
import com.example.vinfan.entity.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface HoaDonChiTietRepo extends JpaRepository<HoaDonChiTiet, Integer> {
    @Query("SELECT new com.example.vinfan.dto.HoaDonChiTietDTO(" +
            "hd.ma, sp.ten, spct.hinh_anh, hdct.soLuong, hdct.gia, hdct.thanhTien) " +
            "FROM HoaDonChiTiet hdct " +
            "JOIN hdct.hoaDon hd " +
            "JOIN hdct.sanPhamChiTiet spct " +
            "JOIN spct.sanPham sp " +
            "WHERE hd.id = :hoaDonId")
    List<HoaDonChiTietDTO> findSanPhamByHoaDonId(@Param("hoaDonId") int hoaDonId);


//
//    @Query("select h from HoaDonChiTiet  h where h.hoaDon.id =:idHoaDon")
//    List<HoaDonChiTiet> findHoaDonChiTietByIdHoaDon(@Param("idHoaDon") int idHoaDon);
}
