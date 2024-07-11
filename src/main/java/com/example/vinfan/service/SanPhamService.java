package com.example.vinfan.service;

import com.example.vinfan.entity.SanPhamChiTiet;
import org.springframework.stereotype.Service;

import java.util.List;
public interface SanPhamService {

    List<SanPhamChiTiet> getAll();

    Boolean create(SanPhamChiTiet sanPhamChiTiet);

    SanPhamChiTiet findById(Long id);

    Boolean update(SanPhamChiTiet sanPhamChiTiet);

    Boolean delete(Long id);
}
