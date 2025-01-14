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

    @Override
    public BigDecimal tongTienThuDuocHomNay() {
        BigDecimal tongTien = thongKeRepo.tinhTongTienThuDuocHomNay();
        return tongTien != null ? tongTien : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal tongTienThuDuocHomQua() {
        // Tính ngày hôm qua
        LocalDate yesterday = LocalDate.now().minusDays(1);
        // Gọi repository
        return thongKeRepo.tinhTongTienThuDuocHomQua(yesterday);
    }
    @Override
    public BigDecimal tinhTongTienThuDuocTrongKhoangThoiGian(LocalDate tuNgay, LocalDate denNgay) {
        return thongKeRepo.timTongTienThuDuocTrongKhoangThoiGian(tuNgay, denNgay);
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

    @Override
    public List<DoanhThuThongKeDTO> getDoanhThuTheoThoiGian(LocalDate tuNgay, LocalDate denNgay) {
        // Tính toán hoặc truy vấn dữ liệu doanh thu theo ngày từ database
        LocalDate startDate = (tuNgay != null) ? tuNgay : LocalDate.now().minusMonths(1); // mặc định 1 tháng
        LocalDate endDate = (denNgay != null) ? denNgay : LocalDate.now();  // mặc định ngày hôm nay

        // Truy vấn dữ liệu từ database
        List<Object[]> results = thongKeRepo.timDoanhThuTheoNgay(startDate, endDate);

        // Chuyển đổi dữ liệu kết quả thành List<DoanhThuThongKeDTO>
        List<DoanhThuThongKeDTO> doanhThuList = new ArrayList<>();
        for (Object[] result : results) {
            // Lấy giá trị ngày từ kết quả và chuyển nó từ java.sql.Date sang LocalDate
            java.sql.Date sqlDate = (java.sql.Date) result[0];
            LocalDate ngay = sqlDate.toLocalDate(); // chuyển đổi từ java.sql.Date sang LocalDate

            BigDecimal tongThu = (BigDecimal) result[1]; // Lấy tổng thu từ kết quả
            doanhThuList.add(new DoanhThuThongKeDTO(ngay, tongThu));
        }

        return doanhThuList;
    }

}
