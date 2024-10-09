package com.example.datn.entity.thuoc_tinh;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chieu_cao")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChieuCao {
    //    CREATE TABLE chieu_cao (
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

    public ChieuCao(Integer chieuCaoId) {
        this.id = chieuCaoId;
    }
//
//    public ChieuCao(ChieuCao chieuCao) {
//    }
}
