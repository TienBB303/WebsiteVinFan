package com.example.datn.repository.ThuocTinhRepo;

import com.example.datn.entity.thuoc_tinh.ChatLieuKhung;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChatLieuKhungRepo extends JpaRepository<ChatLieuKhung, Integer> {
    @Query("SELECT clk FROM ChatLieuKhung clk " +
            "WHERE (LOWER(clk.ten) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "AND (:trang_thai IS NULL OR clk.trang_thai = :trang_thai))")
    Page<ChatLieuKhung> search(String query, Boolean trang_thai, Pageable pageable);

    Optional<ChatLieuKhung> findByTen(String ten);

    @Query("SELECT clk FROM ChatLieuKhung clk " +
            "WHERE LOWER(clk.ten) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<ChatLieuKhung> searchOnlyTen(String query, Pageable pageable);
}
