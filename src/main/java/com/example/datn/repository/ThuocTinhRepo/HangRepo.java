package com.example.datn.repository.ThuocTinhRepo;

import com.example.datn.entity.thuoc_tinh.Hang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface HangRepo extends JpaRepository<Hang,Integer> {
    @Query("SELECT h FROM Hang h " +
            "WHERE (LOWER(h.ten) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "AND (:trang_thai IS NULL OR h.trang_thai = :trang_thai))")
    Page<Hang> search(String query, Boolean trang_thai, Pageable pageable);

    Optional<Hang> findByTen(String ten);

    @Query("SELECT h FROM Hang h " +
            "WHERE LOWER(h.ten) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Hang> searchOnlyTen(String query, Pageable pageable);
}
