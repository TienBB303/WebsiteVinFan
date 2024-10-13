package com.example.datn.entity.thuoc_tinh;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hang")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String ten;

    Boolean trang_thai;

    public Hang(Integer hangId) {
        this.id = hangId;
    }
}
