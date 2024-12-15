package com.example.datn.repository.ThuocTinhRepo;

import com.example.datn.entity.thuoc_tinh.NutBam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NutBamRepo extends JpaRepository<NutBam, Integer> {
    @Query("SELECT nb FROM NutBam nb " +
            "WHERE (LOWER(nb.ten) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "AND (:trang_thai IS NULL OR nb.trang_thai = :trang_thai))")
    Page<NutBam> search(String query, Boolean trang_thai, Pageable pageable);

    Optional<NutBam> findByTen(String ten);

    @Query("SELECT nb FROM NutBam nb " +
            "WHERE LOWER(nb.ten) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<NutBam> searchOnlyTen(String query, Pageable pageable);
}
