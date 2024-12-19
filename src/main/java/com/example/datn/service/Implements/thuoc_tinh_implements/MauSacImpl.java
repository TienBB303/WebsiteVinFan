package com.example.datn.service.Implements.thuoc_tinh_implements;

import com.example.datn.entity.thuoc_tinh.MauSac;
import com.example.datn.repository.ThuocTinhRepo.MauSacRepo;
import com.example.datn.service.thuoc_tinh_service.MauSacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MauSacImpl implements MauSacService {
    @Autowired
    MauSacRepo mauSacRepo;

    @Override
    public Page<MauSac> search(String query, Boolean trang_thai, Pageable pageable) {
        if (trang_thai == null) {
            return mauSacRepo.searchOnlyTen(query, pageable);
        }
        return mauSacRepo.search(query, trang_thai, pageable);
    }
}
