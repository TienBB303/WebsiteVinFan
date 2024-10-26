package com.example.datn.service;

import com.example.datn.dto.response.ThongKeResponse;

import java.util.List;

public interface ThongKeService {
    List<ThongKeResponse> getListDay();
    List<ThongKeResponse> getListWeek();
    List<ThongKeResponse> getListMonth();
    List<ThongKeResponse> getListYear();
    int getCurrentDay();
    int getCurrentWeek();
    int getCurrentMonth();
    int getCurrentYear();
}
