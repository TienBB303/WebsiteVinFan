package com.example.datn.service.Implements.thuoc_tinh_implements;

import com.example.datn.entity.thuoc_tinh.ChatLieuCanh;
import com.example.datn.repository.ThuocTinhRepo.ChatLieuCanhRepo;
import com.example.datn.service.thuoc_tinh_service.ChatLieuCanhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ChatLieuCanhImpl implements ChatLieuCanhService {
    @Autowired
    ChatLieuCanhRepo chatLieuCanhRepo;

    @Override
    public Page<ChatLieuCanh> search(String query, Boolean trang_thai, Pageable pageable) {
        if (trang_thai == null) {
            return chatLieuCanhRepo.searchOnlyTen(query, pageable);
        }
        return chatLieuCanhRepo.search(query, trang_thai, pageable);
    }
}
