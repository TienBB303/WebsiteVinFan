package com.example.datn.entity.sale_on;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter@Setter
public class CartItem {
    private Long productId;
    private String name;
    private Double price;
    private int quantity;
    private BigDecimal discountedPrice;
    private String color;
    private String capacity;
    private String imageUrl;
}
