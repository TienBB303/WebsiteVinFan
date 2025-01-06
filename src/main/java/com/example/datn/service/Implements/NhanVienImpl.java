package com.example.datn.service.Implements;

import com.example.datn.entity.NhanVien;
import com.example.datn.repository.NhanVienRepository;
import com.example.datn.service.nhan_vien_service.NhanVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NhanVienImpl implements NhanVienService {
    @Autowired
    NhanVienRepository nhanVienRepository;

    public String taoMaTuDong() {
        String lastCode = nhanVienRepository.findMaxCode();
        int nextCode = 1;

        if (lastCode != null && !lastCode.isEmpty()) {
            try {
                // Lấy phần số từ mã cuối cùng và tăng nó lên 1
                nextCode = Integer.parseInt(lastCode.replaceAll("[^0-9]", "")) + 1;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (nextCode <= 999) {
            return String.format("NV%03d", nextCode);
        } else {
            return String.format("NV%d", nextCode);
        }
    }

    @Override
    public Page<NhanVien> search(String keyword, Boolean trang_thai, Pageable pageable) {
        if (trang_thai == null) {
            return nhanVienRepository.searchNhanVienKhongCoTrangThai(keyword, pageable);
        }
        return nhanVienRepository.searchNhanVien(keyword,trang_thai, pageable);
    }

    @Override
    public Boolean checkTrungEmailSdt(String email, String sdt) {
        return nhanVienRepository.findTonTaiEmailvaSdt(email, sdt);
    }

    @Override
    public Boolean checkQuanLyCuoiCungHoatDong() {
        List<NhanVien> listQuanLy = nhanVienRepository.quanLyHoatDong();
        System.out.println("List of active managers: " + listQuanLy.size());
        if (listQuanLy.size() == 1){
            return true;
        }
        return false;
    }

}
