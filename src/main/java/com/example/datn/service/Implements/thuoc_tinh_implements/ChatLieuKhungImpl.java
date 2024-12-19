package com.example.datn.service.Implements.thuoc_tinh_implements;

import com.example.datn.entity.thuoc_tinh.ChatLieuKhung;
import com.example.datn.repository.ThuocTinhRepo.ChatLieuKhungRepo;
import com.example.datn.service.thuoc_tinh_service.ChatLieuKhungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ChatLieuKhungImpl implements ChatLieuKhungService {
    @Autowired
    ChatLieuKhungRepo chatLieuKhungRepo;

    @Override
    public Page<ChatLieuKhung> search(String query, Boolean trang_thai, Pageable pageable) {
        if (trang_thai == null) {
            return chatLieuKhungRepo.searchOnlyTen(query, pageable);
        }
        return chatLieuKhungRepo.search(query, trang_thai, pageable);
    }
}
