package com.example.datn.entity.thuoc_tinh;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_lieu_canh")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatLieuCanh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String ten;

    Boolean trang_thai;

    public ChatLieuCanh(Integer chatLieuCanhId) {
        this.id = chatLieuCanhId;
    }
}
