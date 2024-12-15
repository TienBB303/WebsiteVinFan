package com.example.datn.repository.ThuocTinhRepo;

import com.example.datn.entity.thuoc_tinh.CheDoGio;
import com.example.datn.entity.thuoc_tinh.CheDoGio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CheDoGioRepo extends JpaRepository<CheDoGio, Integer> {
    @Query("SELECT cdg FROM CheDoGio cdg " +
            "WHERE (LOWER(cdg.ten) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "AND (:trang_thai IS NULL OR cdg.trang_thai = :trang_thai))")
    Page<CheDoGio> search(String query, Boolean trang_thai, Pageable pageable);

    Optional<CheDoGio> findByTen(String ten);

    @Query("SELECT cdg FROM CheDoGio cdg " +
            "WHERE LOWER(cdg.ten) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<CheDoGio> searchOnlyTen(String query, Pageable pageable);
}
