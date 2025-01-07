package com.example.datn.controller.ThongKe;

import com.example.datn.dto.Thongke.HoaDonDoanhThuDTO;
import com.example.datn.dto.Thongke.SanPhamBanChayDTO;
import com.example.datn.dto.response.ThongKeResponse;
import com.example.datn.dto.response.ThongKeSanPhamResponse;
import com.example.datn.entity.HoaDon;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.service.thong_ke_service.ThongKeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/thong-ke")

public class ThongKeController {
    private final ThongKeService thongKeService;

    @GetMapping("/index")
    public String getThongKe(
            @RequestParam(name = "tuNgay", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tuNgay,
            @RequestParam(name = "denNgay", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate denNgay,
            Model model) {
        //Hôm nay :
        List<HoaDon> hoaDonHoanThanhHomNay = thongKeService.timHoaDonHoanThanhHomNay();
        BigDecimal tongTienThuHomnay = thongKeService.tongTienThuDuocHomNay();
        model.addAttribute("hoaDonHomNay", hoaDonHoanThanhHomNay);
        model.addAttribute("demSoHoaDonHoanThanhHomnay", hoaDonHoanThanhHomNay.size());
        model.addAttribute("tongTienThuHomnay", tongTienThuHomnay);

        //tìm kiếm bán chạy
        List<SanPhamBanChayDTO> listSPBanChay = thongKeService.listBanChay(tuNgay,denNgay);
        List<HoaDonDoanhThuDTO> listHoaDonBanChay = thongKeService.listHoaDonTheoDoanhThu(tuNgay,denNgay);
        System.out.println("listBanchay :" + listSPBanChay.size());
        System.out.println("listHoaDonBanChay :" + listHoaDonBanChay.size());
        model.addAttribute("listSPBanChay", listSPBanChay);
        model.addAttribute("listHoaDonBanChay", listHoaDonBanChay);
        model.addAttribute("tuNgay", tuNgay != null ? tuNgay : "");
        model.addAttribute("denNgay", denNgay != null ? denNgay : "");

        return "admin/thong_ke/index";
    }

    @GetMapping("/san-pham-ban-chay-api")
    @ResponseBody
    public List<SanPhamBanChayDTO> getSanPhamBanChay(@RequestParam(name = "tuNgay", required = false) LocalDate tuNgay,
                                                     @RequestParam(name = "denNgay", required = false) LocalDate denNgay) {
        return thongKeService.listBanChay(tuNgay, denNgay);
    }

    @GetMapping("/hoa-don-va-doanh-thu-api")
    @ResponseBody
    public List<HoaDonDoanhThuDTO> getHoaDonvaDoanhThu(@RequestParam(name = "tuNgay", required = false) LocalDate tuNgay,
                                                       @RequestParam(name = "denNgay", required = false) LocalDate denNgay) {
        return thongKeService.listHoaDonTheoDoanhThu(tuNgay, denNgay);
    }
}
