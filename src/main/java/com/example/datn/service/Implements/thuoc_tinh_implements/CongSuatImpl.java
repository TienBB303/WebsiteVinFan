package com.example.datn.service.Implements.thuoc_tinh_implements;

import com.example.datn.entity.thuoc_tinh.CongSuat;
import com.example.datn.repository.ThuocTinhRepo.CongSuatRepo;
import com.example.datn.service.thuoc_tinh_service.CongSuatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CongSuatImpl implements CongSuatService {
    @Autowired
    CongSuatRepo congSuatRepo;

    @Override
    public Page<CongSuat> search(String query, Boolean trang_thai, Pageable pageable) {
        if (trang_thai == null) {
            return congSuatRepo.searchOnlyTen(query, pageable);
        }
        return congSuatRepo.search(query, trang_thai, pageable);
    }
}
