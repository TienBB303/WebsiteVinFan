package com.example.datn.service.thuoc_tinh_service;

import com.example.datn.entity.thuoc_tinh.ChatLieuCanh;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatLieuCanhService {
    Page<ChatLieuCanh> search(String query, Boolean trang_thai, Pageable pageable);
}
