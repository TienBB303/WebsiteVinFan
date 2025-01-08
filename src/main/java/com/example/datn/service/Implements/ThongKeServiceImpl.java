package com.example.datn.service.Implements;

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
import java.util.List;
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

//    @Override
//    public List<SanPhamBanChayDTO> listBanChay(LocalDate tuNgay, LocalDate denNgay) {
//        return thongKeRepo.timSanPhamBanChay(tuNgay, denNgay);
//    }
    @Override
    public List<SanPhamBanChayDTO> listBanChay(LocalDate tuNgay, LocalDate denNgay) {
        List<Object[]> results = thongKeRepo.timSanPhamBanChay(tuNgay, denNgay);
//        return results.stream()
//                .map(result -> new SanPhamBanChayDTO((String) result[0], (Long) result[1]))
//                .collect(Collectors.toList());
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
}
