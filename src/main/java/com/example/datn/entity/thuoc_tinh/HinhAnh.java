package com.example.datn.entity.thuoc_tinh;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hinh_anh")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HinhAnh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String hinh_anh_1;

    String hinh_anh_2;

    String hinh_anh_3;


    public HinhAnh(Integer hinhAnhId) {
        this.id = hinhAnhId;
    }
}
