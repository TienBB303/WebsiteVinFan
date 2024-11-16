package com.example.datn.service;

import com.example.datn.dto.response.LichSuThanhToanResponse;
import com.example.datn.dto.response.ListSanPhamInHoaDonChiTietResponse;
import com.example.datn.dto.response.PggInHoaDonResponse;
import com.example.datn.dto.response.ListSpNewInHoaDonResponse;
import com.example.datn.entity.HoaDon;
import com.example.datn.entity.HoaDonChiTiet;
import com.example.datn.entity.SanPhamChiTiet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface HoaDonService {
    Page<HoaDon> findAll(Pageable pageable);

    void save(HoaDon hoaDon);

    void delete(Long id);

    void saveHoaDonChiTiet(HoaDonChiTiet hoaDonChiTiet);


    Page<HoaDon> findHoaDonAndSortDay(int page, int size);

    List<ListSanPhamInHoaDonChiTietResponse> getSanPhamCTByHoaDonId(Long hoaDonId);

    List<ListSpNewInHoaDonResponse> getSanPhamInHoaDon();

    PggInHoaDonResponse getPGGbyHoaDonId(Long hoaDonId);

    LichSuThanhToanResponse getLSTTByHoaDonId(Long hoaDonId);

    Optional<HoaDon> findById(Long id);

    Optional<SanPhamChiTiet> findByIdSanPhamChiTiet(Long id);

    Page<HoaDon> searchHoaDon(String query, Pageable pageable);

    Page<HoaDon> getAllHoaDonByTrangThai(Integer trangThai, Pageable pageable);

    List<SanPhamChiTiet> getSPCTInHDCT();

    SanPhamChiTiet getIdSPCT(long idSPCT);

    List<HoaDon> getAllHoaDon();

    String generateOrderCode();
}
