package com.example.datn.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ThongKeResponse {
    Integer year;
    Integer month;
    Integer week;
    Integer day;
    BigDecimal tongTien;
    Long tongSanPham;
    public ThongKeResponse(String type, Integer date, BigDecimal tongTien, Long tongSanPham) {
        if("YEAR".equals(type)){
            this.year = date;
        } else if ("MONTH".equals(type)){
            this.month = date;
        }else if ("WEEK".equals(type)){
            this.week = date;
        }else if ("DAY".equals(type)){
            this.day = date;
        }
        this.tongTien = tongTien;
        this.tongSanPham = tongSanPham;
    }


}
