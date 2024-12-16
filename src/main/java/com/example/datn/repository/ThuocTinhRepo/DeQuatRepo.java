package com.example.datn.repository.ThuocTinhRepo;

import com.example.datn.entity.thuoc_tinh.DeQuat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DeQuatRepo extends JpaRepository<DeQuat,Integer> {
    @Query("SELECT dq FROM DeQuat dq " +
            "WHERE (LOWER(dq.ten) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "AND (:trang_thai IS NULL OR dq.trang_thai = :trang_thai))")
    Page<DeQuat> search(String query, Boolean trang_thai, Pageable pageable);

    Optional<DeQuat> findByTen(String ten);

    @Query("SELECT dq FROM DeQuat dq " +
            "WHERE LOWER(dq.ten) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<DeQuat> searchOnlyTen(String query, Pageable pageable);
}
