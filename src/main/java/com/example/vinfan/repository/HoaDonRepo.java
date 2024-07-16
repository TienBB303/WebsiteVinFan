package com.example.vinfan.repository;

import com.example.vinfan.entity.HoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoaDonRepo extends JpaRepository<HoaDon, Integer> {
    @Query("select hd from HoaDon hd order by hd.ngayTao desc ")
    Page<HoaDon> findHoaDonAndSortDay(Pageable pageable);

    //    @Query("select new HoaDonDTO(" +
//            "hd.id, kh.ten, nv.ma, tt.hinhThucThanhToan, pg.ten, hd.ma, hd.tenNguoiNhan, hd.sdtNguoiNhan, " +
//            "hd.tongTien, hd.tongTienSauGiamGia, hd.diaChi, hd.phiVanChuyen, hd.ghiChu, hd.loaiHoaDon, hd.ngayTao, " +
//            "hd.ngaySua,hd.nguoiTao,hd.nguoiSua, hd.trangThai) " +
//            "from HoaDon hd " +
//            "join KhachHang kh on hd.idKhachHang = kh.id " +
//            "join NhanVien nv on hd.idNhanVien = nv.id " +
//            "join PhieuGiam pg on hd.idPhieuGiamGia = pg.id " +
//            "join ThanhToan tt on hd.idThanhToan = tt.id")
//    List<HoaDonDTO> getAllList();


}
