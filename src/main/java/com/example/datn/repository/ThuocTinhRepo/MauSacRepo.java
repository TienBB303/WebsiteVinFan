package com.example.datn.repository.ThuocTinhRepo;

import com.example.datn.entity.thuoc_tinh.KieuQuat;
import com.example.datn.entity.thuoc_tinh.MauSac;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MauSacRepo extends JpaRepository<MauSac,Integer> {
    @Query("SELECT ms FROM MauSac ms " +
            "WHERE (LOWER(ms.ten) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "AND (:trang_thai IS NULL OR ms.trang_thai = :trang_thai))")
    Page<MauSac> search(String query, Boolean trang_thai, Pageable pageable);

    Optional<MauSac> findByTen(String ten);

    @Query("SELECT ms FROM MauSac ms " +
            "WHERE LOWER(ms.ten) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<MauSac> searchOnlyTen(String query, Pageable pageable);
}
