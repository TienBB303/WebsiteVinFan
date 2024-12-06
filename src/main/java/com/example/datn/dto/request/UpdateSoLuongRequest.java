package com.example.datn.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateSoLuongRequest {
    Long idSP;
    int soLuong;
}
