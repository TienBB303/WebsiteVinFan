package com.example.datn.service.Implements;

import com.example.datn.entity.SanPham;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.repository.SPCTRepo;
import com.example.datn.repository.SanPhamRepo;
import com.example.datn.repository.phieu_giam_repo.PhieuGiamRepo;
import com.example.datn.service.phieu_giam_service.PhieuGiamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class PhieuGiamImpl implements PhieuGiamService {
    @Autowired
    private PhieuGiamRepo pgRepo;
    @Autowired
    private SPCTRepo spctRepo;
    @Autowired
    private SanPhamRepo sanPhamRepository;

    @Override
    public String taoMaTuDong() {
        String lastCode = pgRepo.findMaxCode();
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
        return String.format("PGG%03d", nextCode);
    }


    @Override
    // Tìm sản phẩm theo ID
    public SanPham findById(Long id) {
        Optional<SanPham> sanPham = sanPhamRepository.findById(id);
        return sanPham.orElse(null); // Trả về null nếu không tìm thấy
    }

    @Override
    public SanPhamChiTiet findDetailById(Long id) {  // Đổi tên phương thức
        Optional<SanPhamChiTiet> sanPhamChiTiet = spctRepo.findById(id);
        return sanPhamChiTiet.orElse(null); // Trả về null nếu không tìm thấy
    }
}
