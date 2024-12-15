package com.example.datn.service.Implements.thuoc_tinh_implements;

import com.example.datn.entity.thuoc_tinh.DeQuat;
import com.example.datn.repository.ThuocTinhRepo.DeQuatRepo;
import com.example.datn.service.thuoc_tinh_service.DeQuatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DeQuatImpl implements DeQuatService {
    @Autowired
    DeQuatRepo deQuatRepo;

    @Override
    public Page<DeQuat> search(String query, Boolean trang_thai, Pageable pageable) {
        if (trang_thai == null) {
            return deQuatRepo.searchOnlyTen(query, pageable);
        }
        return deQuatRepo.search(query, trang_thai, pageable);
    }
}
