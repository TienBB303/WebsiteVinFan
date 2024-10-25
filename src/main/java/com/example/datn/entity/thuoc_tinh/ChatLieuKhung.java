package com.example.datn.entity.thuoc_tinh;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_lieu_khung")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatLieuKhung {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String ten;

    Boolean trang_thai;

    public ChatLieuKhung(Integer chatLieuKhungId) {
        this.id = chatLieuKhungId;
    }
}
