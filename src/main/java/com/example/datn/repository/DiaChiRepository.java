package com.example.datn.repository;

import com.example.datn.entity.DiaChi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaChiRepository extends JpaRepository<DiaChi, Integer> {

    @Query("SELECT d FROM DiaChi d WHERE d.khachHang.id = :khachHangId")
    List<DiaChi> findByKhachHangId(@Param("khachHangId") Integer khachHangId);

    @Query("SELECT d FROM DiaChi d WHERE d.khachHang.id = :khachHangId AND d.trangThai = true")
    DiaChi DiaChimacDinhvsfindByKhachHangId(@Param("khachHangId") Integer khachHangId);

}