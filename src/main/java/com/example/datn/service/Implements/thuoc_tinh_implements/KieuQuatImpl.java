package com.example.datn.service.Implements.thuoc_tinh_implements;

import com.example.datn.entity.thuoc_tinh.KieuQuat;
import com.example.datn.repository.ThuocTinhRepo.KieuQuatRepo;
import com.example.datn.service.thuoc_tinh_service.KieuQuatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class KieuQuatImpl implements KieuQuatService {
    @Autowired
    KieuQuatRepo kieuQuatRepo;

    @Override
    public Page<KieuQuat> search(String query, Boolean trang_thai, Pageable pageable) {
        if (trang_thai == null) {
            return kieuQuatRepo.searchOnlyTen(query, pageable);
        }
        return kieuQuatRepo.search(query, trang_thai, pageable);
    }
}
