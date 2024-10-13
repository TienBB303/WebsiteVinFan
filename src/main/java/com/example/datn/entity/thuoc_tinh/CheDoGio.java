package com.example.datn.entity.thuoc_tinh;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "che_do_gio")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheDoGio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String ten;

    Boolean trang_thai;

    public CheDoGio(Integer cheDoGioId) {
        this.id = cheDoGioId;
    }
}
