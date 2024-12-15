package com.example.datn.repository.ThuocTinhRepo;

import com.example.datn.entity.thuoc_tinh.DuongKinhCanh;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DuongKinhCanhRepo extends JpaRepository<DuongKinhCanh, Integer> {
    @Query("SELECT dkc FROM DuongKinhCanh dkc " +
            "WHERE (LOWER(dkc.ten) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "AND (:trang_thai IS NULL OR dkc.trang_thai = :trang_thai))")
    Page<DuongKinhCanh> search(String query, Boolean trang_thai, Pageable pageable);

    Optional<DuongKinhCanh> findByTen(String ten);

    @Query("SELECT dkc FROM DuongKinhCanh dkc " +
            "WHERE LOWER(dkc.ten) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<DuongKinhCanh> searchOnlyTen(String query, Pageable pageable);
}
