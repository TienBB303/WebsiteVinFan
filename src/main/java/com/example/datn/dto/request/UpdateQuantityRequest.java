package com.example.datn.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter

public class UpdateQuantityRequest {
    private Long itemId;
    private int quantity;
}
