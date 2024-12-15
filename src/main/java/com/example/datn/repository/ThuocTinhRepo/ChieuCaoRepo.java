package com.example.datn.repository.ThuocTinhRepo;

import com.example.datn.entity.thuoc_tinh.ChieuCao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChieuCaoRepo extends JpaRepository<ChieuCao,Integer> {
    @Query("SELECT cc FROM ChieuCao cc " +
            "WHERE (LOWER(cc.ten) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "AND (:trang_thai IS NULL OR cc.trang_thai = :trang_thai))")
    Page<ChieuCao> search(String query, Boolean trang_thai, Pageable pageable);

    Optional<ChieuCao> findByTen(String ten);

    @Query("SELECT cc FROM ChieuCao cc " +
            "WHERE LOWER(cc.ten) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<ChieuCao> searchOnlyTen(String query, Pageable pageable);
}
