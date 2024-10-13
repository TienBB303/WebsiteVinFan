package com.example.datn.entity.thuoc_tinh;

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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String ten;

    Boolean trang_thai;

    public CongSuat(Integer congSuatId) {
        this.id = congSuatId;
    }
}
