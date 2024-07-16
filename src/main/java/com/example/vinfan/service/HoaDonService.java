package com.example.vinfan.service;

import com.example.vinfan.dto.HoaDonChiTietDTO;
import com.example.vinfan.entity.HoaDon;
import org.springframework.data.domain.Page;

import java.util.List;

public interface HoaDonService {
    List<HoaDon> findAll();

    void save(HoaDon hoaDon);

    void delete(int id);

    Page<HoaDon> findHoaDonAndSortDay(int page, int size);

    List<HoaDonChiTietDTO> getSanPhamByHoaDonId(int hoaDonId);

    HoaDon findById(Integer id);
}
