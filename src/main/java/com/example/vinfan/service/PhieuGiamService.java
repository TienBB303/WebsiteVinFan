package com.example.vinfan.service;

import com.example.vinfan.entity.PhieuGiam;
import com.example.vinfan.repository.PhieuGiamRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PhieuGiamService {
    @Autowired
    private PhieuGiamRepo pgRepo;

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
        return String.format("SP%03d", nextCode);
    }
}
