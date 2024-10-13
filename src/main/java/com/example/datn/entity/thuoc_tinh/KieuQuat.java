package com.example.datn.entity.thuoc_tinh;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "kieu_quat")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KieuQuat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String ten;

    Boolean trang_thai;

    public KieuQuat(Integer kieuQuatId) {
        this.id = kieuQuatId;
    }
}
