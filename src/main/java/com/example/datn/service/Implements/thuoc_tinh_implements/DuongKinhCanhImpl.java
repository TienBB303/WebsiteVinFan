package com.example.datn.service.Implements.thuoc_tinh_implements;

import com.example.datn.entity.thuoc_tinh.DuongKinhCanh;
import com.example.datn.repository.ThuocTinhRepo.DuongKinhCanhRepo;
import com.example.datn.service.thuoc_tinh_service.DuongKinhCanhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DuongKinhCanhImpl implements DuongKinhCanhService {
    @Autowired
    DuongKinhCanhRepo duongKinhCanhRepo;

    @Override
    public Page<DuongKinhCanh> search(String query, Boolean trang_thai, Pageable pageable) {
        if (trang_thai == null) {
            return duongKinhCanhRepo.searchOnlyTen(query, pageable);
        }
        return duongKinhCanhRepo.search(query, trang_thai, pageable);
    }
}
