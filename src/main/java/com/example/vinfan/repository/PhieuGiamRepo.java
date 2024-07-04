package com.example.vinfan.repository;

import com.example.vinfan.entity.PhieuGiam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhieuGiamRepo extends JpaRepository<PhieuGiam, Long> {
}
