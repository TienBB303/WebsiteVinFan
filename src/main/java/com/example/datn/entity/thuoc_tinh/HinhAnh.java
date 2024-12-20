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

    @Column(name = "hinh_anh_1")
    private String hinh_anh_1;

    @Column(name = "hinh_anh_2")
    private String hinh_anh_2;

    @Column(name = "hinh_anh_3")
    private String hinh_anh_3;

    public HinhAnh(Integer hinhAnhId) {
        this.id = hinhAnhId;
    }
}
