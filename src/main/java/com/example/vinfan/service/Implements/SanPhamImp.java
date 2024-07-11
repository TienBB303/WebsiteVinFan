package com.example.vinfan.service.Implements;

import com.example.vinfan.entity.SanPhamChiTiet;
import com.example.vinfan.repository.SPCTRepo;
import com.example.vinfan.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SanPhamImp implements SanPhamService {
    @Autowired
    private SPCTRepo spctRepo;

    @Override
    public List<SanPhamChiTiet> getAll() {
        return spctRepo.findAll();
    }

    @Override
    public Boolean create(SanPhamChiTiet sanPhamChiTiet) {
        try {
            spctRepo.save(sanPhamChiTiet);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public SanPhamChiTiet findById(Long id) {
        return spctRepo.findById(id).orElse(null);
    }

    @Override
    public Boolean update(SanPhamChiTiet sanPhamChiTiet) {
        if (spctRepo.existsById(sanPhamChiTiet.getId())) {
            spctRepo.save(sanPhamChiTiet);
            return true;
        }
        return false;
    }

    @Override
    public Boolean delete(Long id) {
        if (spctRepo.existsById(id)) {
            spctRepo.deleteById(id);
            return true;
        }
        return false;
    }
}
