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
    //    CREATE TABLE chat_lieu_khung (
//            id INT PRIMARY KEY IDENTITY(1, 1),
//    ma NVARCHAR(255),
//    ten NVARCHAR(255),
//    trang_thai BIT
//);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String ma;

    String ten;

    Boolean trang_thai;

    public ChatLieuKhung(Integer chatLieuKhungId) {
        this.id = chatLieuKhungId;
    }

//    public ChatLieuKhung(ChatLieuKhung chatLieuKhung) {
//    }
}
