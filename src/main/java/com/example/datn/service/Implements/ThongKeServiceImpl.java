package com.example.datn.service.Implements;

import com.example.datn.dto.response.ThongKeResponse;
import com.example.datn.dto.response.ThongKeSanPhamResponse;
import com.example.datn.repository.ThongKeRepo;
import com.example.datn.service.ThongKeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ThongKeServiceImpl implements ThongKeService {
    private final ThongKeRepo thongKeRepo;

    @Override
    public List<ThongKeResponse> getListDay() {
        return thongKeRepo.getListDay();
    }

    @Override
    public List<ThongKeResponse> getListWeek() {
        return thongKeRepo.getListWeek();
    }

    @Override
    public List<ThongKeResponse> getListMonth() {
        return thongKeRepo.getListMonth();
    }

    @Override
    public List<ThongKeResponse> getListYear() {
        return thongKeRepo.getListYear();
    }

    @Override
    public List<ThongKeSanPhamResponse> getSanPhamBanChay() {
        return thongKeRepo.findSanPhamBanChay();
    }

    @Override
    public int getCurrentDay() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.getDayOfMonth();
    }

    @Override
    public int getCurrentWeek() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
    }

    @Override
    public int getCurrentMonth() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.getMonthValue();
    }

    @Override
    public int getCurrentYear() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.getYear();
    }

}
