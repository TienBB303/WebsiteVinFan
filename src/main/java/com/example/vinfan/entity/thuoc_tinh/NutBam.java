package com.example.vinfan.entity.thuoc_tinh;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nut_bam")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NutBam {
    //    CREATE TABLE nut_bam (
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

    public NutBam(Integer nutBamId) {
        this.id = nutBamId;
    }
}
