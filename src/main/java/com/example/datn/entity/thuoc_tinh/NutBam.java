package com.example.datn.entity.thuoc_tinh;

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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String ten;

    Boolean trang_thai;

    public NutBam(Integer nutBamId) {
        this.id = nutBamId;
    }
}
