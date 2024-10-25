package com.example.datn.entity.thuoc_tinh;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "de_quat")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeQuat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String ten;

    Boolean trang_thai;

    public DeQuat(Integer deQuatId) {
        this.id = deQuatId;
    }
}
