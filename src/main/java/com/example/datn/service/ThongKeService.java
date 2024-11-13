package com.example.datn.service;

import com.example.datn.dto.response.ThongKeResponse;
import com.example.datn.dto.response.ThongKeSanPhamResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ThongKeService {
    List<ThongKeResponse> getListDay();

    List<ThongKeResponse> getListWeek();

    List<ThongKeResponse> getListMonth();

    List<ThongKeResponse> getListYear();

    List<ThongKeSanPhamResponse> getSanPhamBanChay();

    int getCurrentDay();

    int getCurrentWeek();

    int getCurrentMonth();

    int getCurrentYear();
}
