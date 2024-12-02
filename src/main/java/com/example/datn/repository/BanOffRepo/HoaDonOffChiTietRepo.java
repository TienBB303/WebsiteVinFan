package com.example.datn.repository.BanOffRepo;

import com.example.datn.entity.HoaDonOff;
import com.example.datn.entity.HoaDonOffChiTiet;
import com.example.datn.entity.san_pham.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HoaDonOffChiTietRepo extends JpaRepository<HoaDonOffChiTiet, Long> {

    List<HoaDonOffChiTiet> findAllById(Long hoaDonOffId);

    Optional<HoaDonOffChiTiet> findByHoaDonOffAndSanPhamChiTiet(HoaDonOff hoaDonOff, SanPhamChiTiet sanPhamChiTiet);
}
