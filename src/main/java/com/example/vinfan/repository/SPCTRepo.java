package com.example.vinfan.repository;

import com.example.vinfan.entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SPCTRepo extends JpaRepository<SanPhamChiTiet, Long> {
}
