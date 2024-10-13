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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String ten;

    Boolean trang_thai;

    public ChieuCao(Integer chieuCaoId) {
        this.id = chieuCaoId;
    }
}
