package com.example.datn.service.Implements;

import com.example.datn.dto.response.ThongKeResponse;
import com.example.datn.dto.response.ThongKeSanPhamResponse;
import com.example.datn.repository.ThongKeRepo;
import com.example.datn.service.ThongKeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ThongKeServiceImpl implements ThongKeService {
    private final ThongKeRepo thongKeRepo;

    @Override
    public List<ThongKeResponse> getListDay() {
        return thongKeRepo.getListDay();
    }

//    @Override
//    public List<ThongKeResponse> getListYesterDay() {
//        return thongKeRepo.getListYesterday();
//    }

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

    //    @Override
//    public List<ThongKeSanPhamResponse> getSanPhamBanChay() {
//        return thongKeRepo.findSanPhamBanChay();
//    }
    public List<ThongKeSanPhamResponse> getSanPhamBanChay() {
        List<ThongKeSanPhamResponse> allProducts = thongKeRepo.findSanPhamBanChay();
        System.out.println("Dữ liệu từ repo: " + allProducts);
        return allProducts.stream()
                .sorted(Comparator.comparingLong(ThongKeSanPhamResponse::getSoLuongBan).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public int getCurrentDay() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.getDayOfMonth();
    }

    @Override
    public int getYesterday() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return yesterday.getDayOfMonth();
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

    //    TIenBB
    public ThongKeResponse getDifferenceBetweenDays(List<ThongKeResponse> todayList, List<ThongKeResponse> yesterdayList, int currentDay) {
        ThongKeResponse today = todayList.stream()
                .filter(item -> item.getDay() == currentDay)
                .findFirst()
                .orElse(null);

        ThongKeResponse yesterday = yesterdayList.stream()
                .findFirst()
                .orElse(null);

        if (today != null && yesterday != null) {
            BigDecimal difference = today.getTongTienSauGiamGia().subtract(yesterday.getTongTienSauGiamGia());
            return new ThongKeResponse("DIFFERENCE", currentDay, difference, 0L); // hoặc dùng đối tượng thích hợp
        }
        return null; // Nếu không có dữ liệu
    }

}
