package com.example.datn.service.thong_ke_service;

import com.example.datn.dto.Thongke.DoanhThuThongKeDTO;
import com.example.datn.dto.Thongke.HoaDonDoanhThuDTO;
import com.example.datn.dto.Thongke.SanPhamBanChayDTO;
import com.example.datn.dto.response.ThongKeResponse;
import com.example.datn.dto.response.ThongKeSanPhamResponse;
import com.example.datn.entity.HoaDon;
import com.example.datn.entity.SanPhamChiTiet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ThongKeService {
    List<HoaDon>timHoaDonHoanThanhHomNay();

    BigDecimal tongTienThuDuocHomNay();

    List<SanPhamBanChayDTO>listBanChay(LocalDate tuNgay, LocalDate denNgay);

    List<HoaDonDoanhThuDTO> listHoaDonTheoDoanhThu(LocalDate tuNgay, LocalDate denNgay);

    List<DoanhThuThongKeDTO> getDoanhThuTheoThoiGian(String thoiGian, LocalDate tuNgay, LocalDate denNgay);

//    List<DoanhThuThongKeDTO> thongKeDoanhThu(String loaiThongKe, LocalDate tuNgay, LocalDate denNgay);
}
