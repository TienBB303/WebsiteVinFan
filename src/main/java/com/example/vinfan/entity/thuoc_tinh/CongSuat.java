package com.example.vinfan.entity.thuoc_tinh;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cong_suat")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CongSuat {
//    CREATE TABLE cong_suat (
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
}
