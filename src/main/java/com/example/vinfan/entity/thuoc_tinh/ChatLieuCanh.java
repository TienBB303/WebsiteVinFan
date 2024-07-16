package com.example.vinfan.entity.thuoc_tinh;

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
    //    CREATE TABLE chat_lieu_canh (
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

    public ChatLieuCanh(Integer chatLieuCanhId) {
        this.id = chatLieuCanhId;
    }
}
