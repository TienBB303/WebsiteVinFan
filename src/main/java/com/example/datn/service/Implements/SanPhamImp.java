package com.example.datn.service.Implements;


import com.example.datn.entity.SanPham;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.entity.thuoc_tinh.CongSuat;
import com.example.datn.entity.thuoc_tinh.MauSac;
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
import java.util.Optional;

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

    @Override
    public List<SanPhamChiTiet> findAll() {
//        List<SanPhamChiTiet> listSanPhamChiTiet = spctRepo.findAll();
        return spctRepo.findAll();
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
    public Boolean update(SanPham sanPham) {
        if (sanPhamRepo.existsById(sanPham.getId())) {
            sanPhamRepo.save(sanPham);
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

        // Trả về mã mới dưới dạng "SP" cộng với số đã tăng, định dạng thành 4 chữ số tối đa
        if (nextCode <= 999) {
            return String.format("SP%03d", nextCode);  // Sử dụng 3 chữ số, max là SP999
        } else {
            return String.format("SP%d", nextCode);    // Nếu quá 999, sẽ không định dạng, để tạo ra mã SP1000, SP1001...
        }
    }


    @Override
    public Page<SanPhamChiTiet> searchProducts(String query, BigDecimal minPrice, BigDecimal maxPrice, Boolean trang_thai, Pageable pageable) {
        if (trang_thai == null) {
            return spctRepo.searchProductsWithouttrangThai(query, minPrice, maxPrice, pageable);
        }
        return spctRepo.searchProducts(query, minPrice, maxPrice,trang_thai, pageable);
    }

    @Override
    public BigDecimal getSanPhamGiaLonNhat() {
        return spctRepo.findMaxPrice();
    }

    @Override
    public BigDecimal getSanPhamGiaNhoNhat() {
        return spctRepo.findMinPrice();
    }

    @Override
    public List<SanPhamChiTiet> timSanPhamTheoTen(String ten) {
        return spctRepo.timKiemTheoTen(ten);
    }

    @Override
    public Boolean tatCaSanPhamTrangThaiOff(Long sanPhamId) {
        List<SanPhamChiTiet> list = spctRepo.findBySanPhamId(sanPhamId);
        return list.stream().allMatch(chiTiet -> !chiTiet.getTrang_thai());
    }

    @Override
    public Boolean motSanPhamTrangThaiOn(Long sanPhamId) {
        List<SanPhamChiTiet> list = spctRepo.findBySanPhamId(sanPhamId);
        return list.stream().anyMatch(SanPhamChiTiet::getTrang_thai);
    }

    @Override
    public Boolean checkTrungLap(String ten, CongSuat congSuat, MauSac mauSac) {
        List<SanPhamChiTiet> listSpct = spctRepo.timKiemTheoTen(ten);

        for (SanPhamChiTiet sp : listSpct) {
            if (sp.getCongSuat().equals(congSuat) && sp.getMauSac().equals(mauSac)) {
                return true; // Trùng lặp
            }
        }
        return false; // Không trùng
    }

    @Override
    public Boolean checkHetSoLuong(Long sanPhamId) {
        List<SanPhamChiTiet> listSpct = spctRepo.findBySanPhamId(sanPhamId);
        for (SanPhamChiTiet sp : listSpct) {
            if (sp.getSo_luong() <= 0) {
                return true; // hết hàng
            }
        }
        return false; // Còn hàng
    }

    //khoi
    public SanPhamChiTiet getProductDetails(Long productId) {
        // Truy vấn sản phẩm chi tiết từ bảng san_pham_chi_tiet
        Optional<SanPhamChiTiet> sanPhamChiTiet = spctRepo.findChiTietById(productId);

        // Nếu tìm thấy sản phẩm chi tiết, trả về nó
        if (sanPhamChiTiet.isPresent()) {
            return sanPhamChiTiet.get();
        } else {
            // Nếu không tìm thấy, trả về null hoặc có thể xử lý theo nhu cầu
            return null;
        }
    }

    @Override
    public Page<SanPham> findByMaOrTen(String ten, Pageable pageable) {
        return sanPhamRepo.findByTenOrMa(ten, pageable);
    }
}
