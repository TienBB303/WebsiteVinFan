package com.example.vinfan.repository;

import com.example.vinfan.entity.DiaChi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiaChiRepository extends JpaRepository<DiaChi, Integer> {

    @Query("SELECT d FROM DiaChi d WHERE d.khachHang.id = :khachHangId")
    DiaChi findByKhachHangId(@Param("khachHangId") Integer khachHangId);
}
