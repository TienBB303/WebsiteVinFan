package com.example.datn.repository;

import com.example.datn.entity.PhieuGiam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PhieuGiamRepo extends JpaRepository<PhieuGiam, Integer> {
    @Query("SELECT MAX(pgg.ma) FROM PhieuGiam pgg")
    String findMaxCode();

    Page<PhieuGiam> findByTenLikeAndTrangThai(String ten, boolean trangThai, Pageable pageable);

    Page<PhieuGiam> findByTenLike(String ten, Pageable pageable);
}
