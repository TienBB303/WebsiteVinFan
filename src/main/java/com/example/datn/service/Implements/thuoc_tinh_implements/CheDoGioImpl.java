package com.example.datn.service.Implements.thuoc_tinh_implements;

import com.example.datn.entity.thuoc_tinh.CheDoGio;
import com.example.datn.repository.ThuocTinhRepo.CheDoGioRepo;
import com.example.datn.service.thuoc_tinh_service.CheDoGioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CheDoGioImpl implements CheDoGioService {
    @Autowired
    CheDoGioRepo cheDoGioRepo;

    @Override
    public Page<CheDoGio> search(String query, Boolean trang_thai, Pageable pageable) {
        if (trang_thai == null) {
            return cheDoGioRepo.searchOnlyTen(query, pageable);
        }
        return cheDoGioRepo.search(query, trang_thai, pageable);
    }
}
