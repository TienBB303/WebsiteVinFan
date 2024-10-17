package com.example.datn.entity.thuoc_tinh;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "duong_kinh_canh")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DuongKinhCanh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String ten;

    Boolean trang_thai;

    public DuongKinhCanh(Integer duongKinhCanhId) {
        this.id = duongKinhCanhId;
    }
}
