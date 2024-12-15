package com.example.datn.service.Implements.thuoc_tinh_implements;

import com.example.datn.entity.thuoc_tinh.ChieuCao;
import com.example.datn.repository.ThuocTinhRepo.ChieuCaoRepo;
import com.example.datn.service.thuoc_tinh_service.ChieuCaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ChieuCaoImpl implements ChieuCaoService {
    @Autowired
    ChieuCaoRepo ChieuCaoRepo;

    @Override
    public Page<ChieuCao> search(String query, Boolean trang_thai, Pageable pageable) {
        if (trang_thai == null) {
            return ChieuCaoRepo.searchOnlyTen(query, pageable);
        }
        return ChieuCaoRepo.search(query, trang_thai, pageable);
    }
}
