package com.example.datn.service.Implements;

import com.example.datn.entity.KhachHang;
import com.example.datn.repository.KhachHangRepo;
import com.example.datn.service.khach_hang_service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KhachHangImpl implements KhachHangService {
    @Autowired
    KhachHangRepo khachHangRepo;

    @Override
    public Page<KhachHang> search(String keyword, Boolean trang_thai, Pageable pageable) {
        if (trang_thai == null) {
            return khachHangRepo.searchKhachHangKhongCoTrangThai(keyword, pageable);
        }
        return khachHangRepo.searchKhachHang(keyword,trang_thai, pageable);
    }

    public String taoMaTuDong() {
        String lastCode = khachHangRepo.findMaxCode();
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
            return String.format("KH%03d", nextCode);  // Sử dụng 3 chữ số, max là SP999
        } else {
            return String.format("KH%d", nextCode);    // Nếu quá 999, sẽ không định dạng, để tạo ra mã SP1000, SP1001...
        }
    }

}
