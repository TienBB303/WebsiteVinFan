package com.example.vinfan.entity.thuoc_tinh;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dieu_khien_tu_xa")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DieuKhienTuXa {
    //    CREATE TABLE dieu_khien_tu_xa (
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
