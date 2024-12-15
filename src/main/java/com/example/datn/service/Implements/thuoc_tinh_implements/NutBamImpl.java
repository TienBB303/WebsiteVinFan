package com.example.datn.service.Implements.thuoc_tinh_implements;

import com.example.datn.entity.thuoc_tinh.NutBam;
import com.example.datn.repository.ThuocTinhRepo.NutBamRepo;
import com.example.datn.service.thuoc_tinh_service.NutBamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class NutBamImpl implements NutBamService {
    @Autowired
    NutBamRepo nutBamRepo;

    @Override
    public Page<NutBam> search(String query, Boolean trang_thai, Pageable pageable) {
        if (trang_thai == null) {
            return nutBamRepo.searchOnlyTen(query, pageable);
        }
        return nutBamRepo.search(query, trang_thai, pageable);
    }
}
