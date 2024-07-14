package com.example.vinfan.repository;

import com.example.vinfan.entity.PhieuGiam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PhieuGiamRepo extends JpaRepository<PhieuGiam, Integer> {
    @Query("SELECT MAX(pgg.ma) FROM PhieuGiam pgg")
    String findMaxCode();
    public Page<PhieuGiam> findByTenLike(String keyword, PageRequest p);
}
