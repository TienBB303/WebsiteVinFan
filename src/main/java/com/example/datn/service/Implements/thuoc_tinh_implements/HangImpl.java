package com.example.datn.service.Implements.thuoc_tinh_implements;

import com.example.datn.entity.thuoc_tinh.Hang;
import com.example.datn.repository.ThuocTinhRepo.HangRepo;
import com.example.datn.service.thuoc_tinh_service.HangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class HangImpl implements HangService {
    @Autowired
    HangRepo hangRepo;

    @Override
    public Page<Hang> search(String query, Boolean trang_thai, Pageable pageable) {
        if (trang_thai == null) {
            return hangRepo.searchOnlyTen(query, pageable);
        }
        return hangRepo.search(query, trang_thai, pageable);
    }
}
