package com.example.datn.service.Implements;

import com.example.datn.entity.SanPham;
import com.example.datn.repository.SanPhamRepo.SanPhamRepo;
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
    private SanPhamRepo sanPhamRepository;

    @Override
    public String taoMaTuDong() {
        // Lấy mã cuối cùng từ cơ sở dữ liệu
        String lastCode = pgRepo.findMaxCode();
        int nextCode = 1; // Mặc định bắt đầu từ 1 nếu không có mã trước đó
        if (lastCode != null && !lastCode.isEmpty()) {
            try {
                // Lấy phần số từ mã và tăng giá trị
                nextCode = Integer.parseInt(lastCode.replaceAll("[^0-9]", "")) + 1;
            } catch (NumberFormatException e) {
                // Ghi log lỗi nếu không thể chuyển đổi số, mã sẽ reset về 1
                e.printStackTrace();
            }
        }
        // Tạo mã mới với định dạng "PGG" cộng số (định dạng 4 chữ số)
        return String.format("PGG%04d", nextCode);
    }


    @Override
    // Tìm sản phẩm theo ID
    public SanPham findById(Long id) {
        Optional<SanPham> sanPham = sanPhamRepository.findById(id);
        return sanPham.orElse(null); // Trả về null nếu không tìm thấy
    }
}
