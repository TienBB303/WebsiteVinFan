package com.example.datn.service.Implements;

import com.example.datn.dto.response.HoaDonChiTietResponse;
import com.example.datn.dto.response.HoaDonResponse;
import com.example.datn.entity.HoaDon;
import com.example.datn.repository.HoaDonChiTietRepo;
import com.example.datn.repository.HoaDonRepo;
import com.example.datn.service.HoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HoaDonServiceImpl implements HoaDonService {
    private final HoaDonRepo hoaDonRepo;
    private final HoaDonChiTietRepo hoaDonChiTietRepo;
    @Override
    public List<HoaDon> findAll() {
        return hoaDonRepo.findAll();
    }

    @Override
    public void save(HoaDon hoaDon) {
        hoaDonRepo.save(hoaDon);
    }

    @Override
    public void delete(int id) {

    }

    @Override
    public Page<HoaDon> findHoaDonAndSortDay(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return hoaDonRepo.findHoaDonAndSortDay(pageable);
    }

    @Override
    public List<HoaDonChiTietResponse> getSanPhamByHoaDonId(int hoaDonId) {
        return hoaDonChiTietRepo.findSanPhamByHoaDonId(hoaDonId);
    }

    @Override
    public HoaDonResponse getPGGbyHoaDonId(int hoaDonId) {
        return hoaDonRepo.findPGGByHoaDonId(hoaDonId);
    }

    @Override
    public Optional<HoaDon> findById(Integer id) {
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(id);
        return hoaDonOptional;
    }

    @Override
    public Page<HoaDon> searchHoaDon(String query, Pageable pageable) {
        return hoaDonRepo.searchHoaDon(query, pageable);
    }

    @Override
    public List<HoaDon> getAllHoaDonByTrangThai(Integer trangThai) {
        return hoaDonRepo.findAllByTrangThai(trangThai);
    }
}
