package com.example.datn.service.Implements;

import com.example.datn.dto.Thongke.DoanhThuThongKeDTO;
import com.example.datn.dto.Thongke.HoaDonDoanhThuDTO;
import com.example.datn.dto.Thongke.SanPhamBanChayDTO;
import com.example.datn.dto.response.ThongKeResponse;
import com.example.datn.entity.HoaDon;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.repository.ThongKeRepo.ThongKeRepo;
import com.example.datn.service.thong_ke_service.ThongKeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ThongKeServiceImpl implements ThongKeService {
    private final ThongKeRepo thongKeRepo;

    @Override
    public List<HoaDon> timHoaDonHoanThanhHomNay() {
        return thongKeRepo.timHoaDonHoanThanhHomNay();
    }

    public BigDecimal tongTienThuDuocHomNay() {
        BigDecimal tongTien = thongKeRepo.tinhTongTienThuDuocHomNay();
        return tongTien != null ? tongTien : BigDecimal.ZERO;
    }

    @Override
    public List<SanPhamBanChayDTO> listBanChay(LocalDate tuNgay, LocalDate denNgay) {
        List<Object[]> results = thongKeRepo.timSanPhamBanChay(tuNgay, denNgay);
        return results.stream()
                .map(result -> new SanPhamBanChayDTO((String) result[0], (Long) result[1]))
                .limit(10)  // Giới hạn kết quả chỉ 10 sản phẩm
                .collect(Collectors.toList());
    }

    @Override
    public List<HoaDonDoanhThuDTO> listHoaDonTheoDoanhThu(LocalDate tuNgay, LocalDate denNgay) {
        List<Object[]> results = thongKeRepo.timHoaDonTheoDoanhThu(tuNgay, denNgay);

        return results.stream()
                .map(result -> new HoaDonDoanhThuDTO((Long) result[0], (BigDecimal) result[1]))
                .limit(10)  // Giới hạn kết quả chỉ 10 hóa đơn doanh thu cao nhất
                .collect(Collectors.toList());
    }

    public List<DoanhThuThongKeDTO> getDoanhThuTheoThoiGian(String thoiGian, LocalDate tuNgay, LocalDate denNgay) {
        List<Object[]> data;

        if ("week".equals(thoiGian)) {
            data = thongKeRepo.getDoanhThuTheoTuan(tuNgay, denNgay);
        } else if ("month".equals(thoiGian)) {
            data = thongKeRepo.getDoanhThuTheoThang(tuNgay, denNgay);
        } else if ("year".equals(thoiGian)) {
            data = thongKeRepo.getDoanhThuTheoNam(tuNgay, denNgay);
        } else {
            data = thongKeRepo.getDoanhThuTheoThoiGian(tuNgay, denNgay);
        }

        // Chuyển đổi Object[] thành DoanhThuThongKeDTO
        List<DoanhThuThongKeDTO> result = new ArrayList<>();

        for (Object[] row : data) {
            if (row.length == 2) {
                LocalDate ngay = (LocalDate) row[0];
                BigDecimal tongDoanhThu = (BigDecimal) row[1];
                result.add(new DoanhThuThongKeDTO(ngay, tongDoanhThu));
            } else if (row.length == 1) {
                Long hoaDonId = (Long) row[0];
                BigDecimal tongDoanhThu = new BigDecimal((Double) row[1]);
                result.add(new DoanhThuThongKeDTO(hoaDonId, tongDoanhThu));
            }
        }

        return result;
    }
//    @Override
//    public List<DoanhThuThongKeDTO> thongKeDoanhThu(String loaiThongKe, LocalDate tuNgay, LocalDate denNgay) {
//        List<Object[]> results = thongKeRepo.thongKeDoanhThuTheoThoiGian(loaiThongKe, tuNgay, denNgay);
//        return results.stream()
//                .map(result -> new DoanhThuThongKeDTO((String) result[0], (BigDecimal) result[1]))
//                .collect(Collectors.toList());
//    }
}
