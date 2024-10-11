package com.example.datn.service;

import com.example.datn.dto.response.HoaDonChiTietResponse;
import com.example.datn.dto.response.HoaDonResponse;
import com.example.datn.entity.HoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface HoaDonService {
    List<HoaDon> findAll();

    void save(HoaDon hoaDon);

    void delete(int id);

    Page<HoaDon> findHoaDonAndSortDay(int page, int size);

    List<HoaDonChiTietResponse> getSanPhamByHoaDonId(int hoaDonId);

    HoaDonResponse getPGGbyHoaDonId(int hoaDonId);

    Optional<HoaDon> findById(Integer id);

    Page<HoaDon> searchHoaDon(String query, Pageable pageable);

    List<HoaDon> getAllHoaDonByTrangThai(Integer trangThai);
}
