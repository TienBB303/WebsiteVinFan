package com.example.datn.service.Implements;

import com.example.datn.dto.request.SPCTRequest;
import com.example.datn.dto.response.HinhThucThanhToanResponse;
import com.example.datn.entity.*;
import com.example.datn.repository.*;
import com.example.datn.service.BanHangTaiQuay.BanHangTaiQuayService;
import com.example.datn.service.HoaDonService;
import com.example.datn.service.TrangThaiHoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BanHangTaiQuayServiceImpl implements BanHangTaiQuayService {
    private final HoaDonService hoaDonService;
    private final TrangThaiHoaDonService trangThaiHoaDonService;
    private final HoaDonRepo hoaDonRepo;
    private final LichSuHoaDonRepo lichSuHoaDonRepo;
    private final SPCTRepo spctRepo;
    private final KhachHangRepo khachHangRepo;
    Long myHoaDon = null;


    @Override
    @Transactional
    public void taoHoaDonCho(HoaDon hoaDon) {
        List<HoaDon> listHoaDon = hoaDonService.getAll();
        int count = (int) listHoaDon.stream()
                .filter(sl -> sl.getTrangThai() == trangThaiHoaDonService.getTrangThaiHoaDonRequest().getHoaDonCho())
                .count();
        if (count < 5) {
            //tao hoa don
            hoaDon.setMa(hoaDonService.generateOrderCode());
            hoaDon.setTrangThai(trangThaiHoaDonService.getTrangThaiHoaDonRequest().getHoaDonCho());
            hoaDon.setNgayTao(LocalDate.now());
            hoaDon.setLoaiHoaDon(true);
            // Lấy khách hàng và thêm tên vào hoaDon
            KhachHang khachHang = getKhachHangLe(1L);
            hoaDon.setKhachHang(khachHang);
            hoaDon.setTenNguoiNhan(khachHang.getTen());
            HinhThucThanhToanResponse hinhThucThanhToanResponse = getHinhThucThanhToan();
            hoaDon.setHinhThucThanhToan(hinhThucThanhToanResponse.getTienMat());
            hoaDonRepo.saveAndFlush(hoaDon);

            //tao lich su hoa don
            LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
            lichSuHoaDon.setHoaDon(hoaDon);
            lichSuHoaDon.setNgayTao(hoaDon.getNgayTao());
            lichSuHoaDon.setTrangThai(hoaDon.getTrangThai());
            lichSuHoaDonRepo.save(lichSuHoaDon);

        }
    }

    @Override
    public void addSPToHoaDonCho(Long idSP) {
//        System.out.println("id sp là : " + idSP);
//        if (myHoaDon == null) {
//            throw new IllegalStateException("Vui lòng chọn hóa đơn.");
//        }
//        List<SPCTRequest> listSPCT = hoaDonCho.get(myHoaDon);
//        if (listSPCT == null) {
//            listSPCT = new ArrayList<>();
//            hoaDonCho.put(myHoaDon, listSPCT);
//        }
//        //call spct
//        SanPhamChiTiet sanPhamChiTiet = spctRepo.findById(idSP).orElseThrow();
//        // tạo 1 đối tượng sp mới
//        SPCTRequest request = new SPCTRequest(sanPhamChiTiet.getId(), sanPhamChiTiet.getSanPham().getTen(),
//                sanPhamChiTiet.getSo_luong(), sanPhamChiTiet.getGia());
//        // Thêm sản phẩm vào danh sách hiện tại
//        int soLuongUpdateKhiTrung = 1;
//        boolean checkSP = false;
//        for (int i = 0; i < listSPCT.size(); i++) {
//            if (listSPCT.get(i).getIdSP().equals(request.getIdSP())) {
//                SPCTRequest sanPhamGet = listSPCT.get(i);
//                SPCTRequest spUpdate = new SPCTRequest(sanPhamGet.getIdSP(), sanPhamGet.getTenSP(),
//                        sanPhamGet.getSoLuong() + soLuongUpdateKhiTrung, sanPhamGet.getGia());
//                // sanPhamUpdate.setSoLuong(soLuongUpdateKhiTrung + sanPhamUpdate.getSoLuong());
//                listSPCT.set(i, spUpdate);
//                checkSP = true;
//            }
//        }
//        if (checkSP == false) {
//            listSPCT.add(request);
//        }
//        System.out.println("Danh sách hóa đơn chi tiết sau khi thêm: " + listSPCT);
    }

    @Override
    public List<HoaDon> findHoaDon() {
        return hoaDonRepo.findAll().stream().
                filter(loc -> loc.getTrangThai() == trangThaiHoaDonService.getTrangThaiHoaDonRequest()
                        .getHoaDonCho()).toList();
    }

    @Override
    public Long getIdHoaDon() {
        return myHoaDon;
    }

    @Override
    public BigDecimal getTongTien(Long idHD) {
        HoaDon hoaDon = hoaDonRepo.findById(idHD).orElse(null);
        return hoaDon != null ? hoaDon.getTongTien() : BigDecimal.ZERO;
    }

    @Override
    public KhachHang getKhachHangLe(Long id) {
        return khachHangRepo.findById(id).orElseThrow(() -> new RuntimeException("Customer not found with id " + id));
    }

    @Override
    public HinhThucThanhToanResponse getHinhThucThanhToan() {
        HinhThucThanhToanResponse hinhThucThanhToan = new HinhThucThanhToanResponse("Thanh Toán Khi Nhận Hàng", "Chuyển Khoản", "Tiền Mặt");
        return hinhThucThanhToan;
    }


}
