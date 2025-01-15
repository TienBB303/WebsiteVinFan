package com.example.datn.dto.Thongke;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoanhThuThongKeDTO {
    private LocalDate ngay;
    private BigDecimal tongThu;
}
