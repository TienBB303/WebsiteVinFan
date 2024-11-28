package com.example.datn.dto.request.sale_on_request;

import com.example.datn.entity.sale_on.CartItem;
import lombok.*;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter@Setter
public class CreateHoaDonRequest {
    private String name;
    private String phone;
    private String address;
    private String district;
    private String province;
    private String paymentMethod;
    private String note;
    private List<CartItem> cartItems;
}
