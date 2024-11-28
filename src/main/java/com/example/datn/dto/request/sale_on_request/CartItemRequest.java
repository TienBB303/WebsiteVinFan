package com.example.datn.dto.request.sale_on_request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartItemRequest {
    private Long productId;
    private String name;
    private BigDecimal price;
    private int quantity;
    private BigDecimal discountedPrice;
}
