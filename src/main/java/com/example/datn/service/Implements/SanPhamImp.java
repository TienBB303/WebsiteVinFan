package com.example.datn.service.Implements;


import com.example.datn.entity.SanPham;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.repository.SPCTRepo;
import com.example.datn.repository.SanPhamRepo;
import com.example.datn.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SanPhamImp implements SanPhamService {

    @Autowired
    private SanPhamRepo sanPhamRepo;
    @Autowired
    private SPCTRepo spctRepo;

    @Override
    public Page<SanPhamChiTiet> findAll(Pageable pageable) {
        return spctRepo.findAll(pageable);
    }

    @Transactional
    @Override
    public void create(SanPham sanPham, List<SanPhamChiTiet> sanPhamChiTietList) {
        sanPhamRepo.save(sanPham);
        for (SanPhamChiTiet sanPhamChiTiet : sanPhamChiTietList) {
            spctRepo.save(sanPhamChiTiet);
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

    public String taoMaTuDong() {
        String lastCode = sanPhamRepo.findMaxCode();
        int nextCode = 1;
        if (lastCode != null && !lastCode.isEmpty()) {
            try {
                // Lấy phần số từ mã cuối cùng và tăng nó lên 1
                nextCode = Integer.parseInt(lastCode.replaceAll("[^0-9]", "")) + 1;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        // Trả về mã mới dưới dạng "SP" cộng với số đã tăng, định dạng thành 3 chữ số
        return String.format("SP%03d", nextCode);
    }

    @Override
    public Page<SanPhamChiTiet> searchProducts(String query, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return spctRepo.searchProducts(query, minPrice, maxPrice, pageable);
    }

    @Override
    public BigDecimal getSanPhamGiaLonNhat() {
        return spctRepo.findMaxPrice();
    }
}
