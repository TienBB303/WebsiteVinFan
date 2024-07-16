package com.example.vinfan.service.impl;

import com.example.vinfan.dto.HoaDonChiTietDTO;
import com.example.vinfan.entity.HoaDon;
import com.example.vinfan.repository.HoaDonChiTietRepo;
import com.example.vinfan.repository.HoaDonRepo;
import com.example.vinfan.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HoaDonServiceImpl implements HoaDonService {
    @Autowired
    private HoaDonRepo hoaDonRepo;
    @Autowired
    private HoaDonChiTietRepo hoaDonChiTietRepo;

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
    public List<HoaDonChiTietDTO> getSanPhamByHoaDonId(int hoaDonId) {
        return hoaDonChiTietRepo.findSanPhamByHoaDonId(hoaDonId);
    }

    @Override
    public HoaDon findById(Integer id) {
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(id);
        return hoaDonOptional.orElse(null);
    }
}
