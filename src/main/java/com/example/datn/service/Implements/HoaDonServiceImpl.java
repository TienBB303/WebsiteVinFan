package com.example.datn.service.Implements;

import com.example.datn.dto.request.AddSPToHoaDonChiTietRequest;
import com.example.datn.dto.response.*;
import com.example.datn.entity.*;
import com.example.datn.repository.HoaDonChiTietRepo;
import com.example.datn.repository.HoaDonRepo;
import com.example.datn.repository.LichSuHoaDonRepo;
import com.example.datn.repository.SPCTRepo;
import com.example.datn.service.HoaDonService;
import com.example.datn.service.TrangThaiHoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HoaDonServiceImpl implements HoaDonService {
    private final HoaDonRepo hoaDonRepo;
    private final HoaDonChiTietRepo hoaDonChiTietRepo;
    private final SPCTRepo spctRepo;
    private final TrangThaiHoaDonService trangThaiHoaDonService;
    private final LichSuHoaDonRepo lichSuHoaDonRepo;


    @Override
    public Page<HoaDon> findAll(Pageable pageable) {
        return hoaDonRepo.findAll(pageable);
    }

    @Override
    public List<HoaDon> getAll() {
        return hoaDonRepo.findAll();
    }


    @Override
    public void save(HoaDon hoaDon) {
        hoaDonRepo.save(hoaDon);
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Page<HoaDon> findHoaDonAndSortDay(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return hoaDonRepo.findHoaDonAndSortDay(pageable);
    }

    @Override
    public List<ListSanPhamInHoaDonChiTietResponse> getSanPhamCTByHoaDonId(Long hoaDonId) {
        return hoaDonChiTietRepo.findSanPhamByHoaDonId(hoaDonId);
    }

    @Override
    public List<HoaDonChiTiet> timSanPhamChiTietTheoHoaDon(Long idHoaDon) {
        // Lấy danh sách HoaDonChiTiet theo idHoaDon
        List<HoaDonChiTiet> hoaDonChiTiet = hoaDonChiTietRepo.findByHoaDon_Id(idHoaDon);
        return hoaDonChiTiet;
    }

    @Override
    public List<ListSpNewInHoaDonResponse> getSanPhamInHoaDon() {
        return hoaDonChiTietRepo.findSanPhamInHoaDon();
    }

    @Override
    public PggInHoaDonResponse getPGGbyHoaDonId(Long hoaDonId) {
        return hoaDonRepo.findPGGByHoaDonId(hoaDonId);
    }

    @Override
    public LichSuThanhToanResponse getLSTTByHoaDonId(Long hoaDonId) {
        return hoaDonRepo.findThanhToanHoaDonId(hoaDonId);
    }

    @Override
    public Optional<HoaDon> findById(Long id) {
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(id);
        return hoaDonOptional;
    }

    @Override
    public Optional<SanPhamChiTiet> findByIdSanPhamChiTiet(Long id) {
        Optional<SanPhamChiTiet> sanPhamChiTietOptional = spctRepo.findById(id);
        return sanPhamChiTietOptional;
    }

    @Override
    public Page<HoaDon> searchHoaDon(String query, Pageable pageable) {
        return hoaDonRepo.searchHoaDon(query, pageable);
    }

    @Override
    public Page<HoaDon> getAllHoaDonByTrangThai(Integer trangThai, Pageable pageable) {
        return hoaDonRepo.findAllByTrangThai(trangThai, pageable);
    }

    @Override
    public List<SanPhamChiTiet> getSPCTInHDCT() {
        return hoaDonChiTietRepo.findSanPhamChiTietWithSanPham();
    }

    @Override
    public SanPhamChiTiet getIdSPCT(long idSPCT) {
        return spctRepo.findById(idSPCT)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tìm thấy"));
    }

    @Override
    public void addSpToHoaDonChiTietRequestList(AddSPToHoaDonChiTietRequest request) {
        // Tìm HoaDon theo idHD
        HoaDon hoaDon = hoaDonRepo.findById(request.getIdHD())
                .orElseThrow(() -> new RuntimeException("HoaDon không tồn tại với id: " + request.getIdHD()));

        // Tìm SanPhamChiTiet theo idSP
        SanPhamChiTiet sanPhamChiTiet = spctRepo.findById(request.getIdSP())
                .orElseThrow(() -> new RuntimeException("SanPhamChiTiet không tồn tại với id: " + request.getIdSP()));

        // Chuyển DTO sang Entity
        HoaDonChiTiet hoaDonChiTiet = convertToEntity(request, hoaDon, sanPhamChiTiet);

        // Lưu vào database
        hoaDonChiTietRepo.save(hoaDonChiTiet);
    }

    @Override
    public HoaDonChiTiet convertToEntity(AddSPToHoaDonChiTietRequest request, HoaDon hoaDon, SanPhamChiTiet sanPhamChiTiet) {
        //Kiểm tra nếu sản phẩm đã có trong chi tiết hóa đơn
        Optional<HoaDonChiTiet> existingDetail = hoaDonChiTietRepo.findByHoaDonAndSanPhamChiTiet(hoaDon, sanPhamChiTiet);
        //Số lượng được mua là 1
        int soLuong = 1;

        HoaDonChiTiet hoaDonChiTiet;

        if (existingDetail.isPresent()) {
            // Nếu sản phẩm đã có, cộng thêm số lượng
            hoaDonChiTiet = existingDetail.get();
            int newSoLuong = hoaDonChiTiet.getSoLuong() + 1;  // Cộng thêm 1 sản phẩm
            hoaDonChiTiet.setSoLuong(newSoLuong);

            //Cập nhật lại thành tiền
            BigDecimal thanhTien = hoaDonChiTiet.getGia().multiply(BigDecimal.valueOf(newSoLuong));
            hoaDonChiTiet.setThanhTien(thanhTien);
        } else {
            // Nếu sản phẩm chưa có, tạo chi tiết hóa đơn mới
            hoaDonChiTiet = new HoaDonChiTiet();
            hoaDonChiTiet.setHoaDon(hoaDon);
            hoaDonChiTiet.setSanPhamChiTiet(sanPhamChiTiet);
            hoaDonChiTiet.setGia(request.getGia());
            hoaDonChiTiet.setSoLuong(soLuong);
            // Tính thành tiền cho sản phẩm
            BigDecimal thanhTien = request.getGia().multiply(BigDecimal.valueOf(soLuong));
            hoaDonChiTiet.setThanhTien(thanhTien);
        }

        // Lưu chi tiết hóa đơn vào cơ sở dữ liệu
        hoaDonChiTietRepo.save(hoaDonChiTiet);
        return hoaDonChiTiet;
    }

    @Override
    public boolean xacNhanHoaDon(long id) {
        // Tìm kiếm HoaDon dựa trên ID
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(id);
        List<HoaDonChiTiet> listHDCT = hoaDonChiTietRepo.findByHoaDon_Id(id);

        if (hoaDonOptional.isPresent()) {
            for (HoaDonChiTiet hdct : listHDCT) {
                SanPhamChiTiet spct = hdct.getSanPhamChiTiet();
                int soLuongTon = spct.getSo_luong(); // Số lượng hiện tại trong kho
                int soLuongBan = hdct.getSoLuong();    // Số lượng trong hóa đơn chi tiết

                if (soLuongTon < soLuongBan) {
                    throw new RuntimeException("Số lượng tồn kho không đủ cho sản phẩm: " + spct.getSanPham().getTen());
                }
            }
            HoaDon hoaDon = hoaDonOptional.get();

            // Cập nhật trạng thái của HoaDon
            hoaDon.setTrangThai(trangThaiHoaDonService.getTrangThaiHoaDonRequest().getDaXacNhan());
            hoaDonRepo.save(hoaDon);

            // Tạo lịch sử cập nhật
            LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
            lichSuHoaDon.setHoaDon(hoaDon);
            lichSuHoaDon.setTrangThai(trangThaiHoaDonService.getTrangThaiHoaDonRequest().getDaXacNhan());
            lichSuHoaDon.setNgayTao(LocalDate.now());
            lichSuHoaDonRepo.save(lichSuHoaDon);

            return true;
        }
        return false;// Khong tim thay hoa don
    }

    @Override
    public boolean huyHoaDon(long id) {
        // Tìm kiếm HoaDon dựa trên ID
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(id);

        if (hoaDonOptional.isPresent()) {
            HoaDon hoaDon = hoaDonOptional.get();

            // Cập nhật trạng thái của HoaDon
            hoaDon.setTrangThai(trangThaiHoaDonService.getTrangThaiHoaDonRequest().getHuy());

            // Trả lại số lượng sản phẩm
            for (HoaDonChiTiet hoaDonChiTiet : hoaDon.getHoaDonChiTietList()) {
                SanPhamChiTiet sanPhamChiTiet = hoaDonChiTiet.getSanPhamChiTiet();
                sanPhamChiTiet.setSo_luong(sanPhamChiTiet.getSo_luong() + hoaDonChiTiet.getSoLuong());
                spctRepo.save(sanPhamChiTiet);
            }
            hoaDonRepo.save(hoaDon);

            // Tạo lịch sử cập nhật
            LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
            lichSuHoaDon.setHoaDon(hoaDon);
            lichSuHoaDon.setTrangThai(trangThaiHoaDonService.getTrangThaiHoaDonRequest().getHuy());
            lichSuHoaDon.setNgayTao(LocalDate.now());
            lichSuHoaDonRepo.save(lichSuHoaDon);

            return true;
        }
        return false;// Khong tim thay hoa don
    }

    @Override
    public boolean hoanThanhHoaDon(long id) {
        // Tìm kiếm HoaDon dựa trên ID
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(id);
        List<HoaDonChiTiet> listHDCT = hoaDonChiTietRepo.findByHoaDon_Id(id);

        if (hoaDonOptional.isPresent()) {
            for (HoaDonChiTiet hdct : listHDCT) {
                SanPhamChiTiet spct = hdct.getSanPhamChiTiet();
                int soLuongTon = spct.getSo_luong(); // Số lượng hiện tại trong kho
                int soLuongBan = hdct.getSoLuong();    // Số lượng trong hóa đơn chi tiết

                if (soLuongTon < soLuongBan) {
                    throw new RuntimeException("Số lượng tồn kho không đủ cho sản phẩm: " + spct.getSanPham().getTen());
                }
            }
            HoaDon hoaDon = hoaDonOptional.get();

            // Cập nhật trạng thái của HoaDon
            hoaDon.setTrangThai(trangThaiHoaDonService.getTrangThaiHoaDonRequest().getDaGiaoHang());
            hoaDonRepo.save(hoaDon);

            // Tạo lịch sử cập nhật
            LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
            lichSuHoaDon.setHoaDon(hoaDon);
            lichSuHoaDon.setTrangThai(trangThaiHoaDonService.getTrangThaiHoaDonRequest().getDaGiaoHang());
            lichSuHoaDon.setNgayTao(LocalDate.now());
            lichSuHoaDonRepo.save(lichSuHoaDon);

            return true;
        }
        return false;// Khong tim thay hoa don
    }

    @Override
    @Transactional
    public void updateTongTienHoaDon() {
        hoaDonRepo.updateTongTienHoaDon();
    }

    @Override
    public void deleteSPInHD(Long idSanPhamChiTiet) {
        hoaDonChiTietRepo.deleteSanPhamChiTiet_Id(idSanPhamChiTiet);
    }


    @Override
    public HinhThucThanhToanResponse getHinhThucThanhToan() {
        HinhThucThanhToanResponse hinhThucThanhToan = new HinhThucThanhToanResponse("Thanh Toán Khi Nhận Hàng", "Chuyển Khoản", "Tiền Mặt");
        return hinhThucThanhToan;
    }

    @Override
    public void tangSoLuongSanPham(Long idHoaDon, Long idSanPhamChiTiet) {
        // Lấy HoaDonChiTiet theo idHoaDon và idSanPhamChiTiet
        HoaDonChiTiet hdct = hoaDonChiTietRepo.findByHoaDon_IdAndSanPhamChiTiet_Id(idHoaDon, idSanPhamChiTiet);

        if (hdct != null) {
            // Lấy thông tin sản phẩm chi tiết
            SanPhamChiTiet sanPhamChiTiet = spctRepo.findById(idSanPhamChiTiet)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm chi tiết với ID đã cho."));

            // Kiểm tra số lượng sản phẩm trong kho
            int soLuongTrongKho = sanPhamChiTiet.getSo_luong();
            int soLuongTrongHoaDon = hdct.getSoLuong();

            if (soLuongTrongHoaDon < soLuongTrongKho) {
                // Tăng số lượng lên 1
                hdct.setSoLuong(soLuongTrongHoaDon + 1);

                // Cập nhật lại thành tiền
                BigDecimal gia = hdct.getGia();
                hdct.setThanhTien(gia.multiply(BigDecimal.valueOf(hdct.getSoLuong())));

                // Lưu lại bản ghi HoaDonChiTiet đã cập nhật
                hoaDonChiTietRepo.save(hdct);
            } else {
                throw new RuntimeException("Số lượng sản phẩm trong kho không đủ để thêm.");
            }
        } else {
            throw new RuntimeException("Không tìm thấy hóa đơn chi tiết với ID sản phẩm chi tiết.");
        }
    }


    @Override
    public void giamSoLuongSanPham(Long idHoaDon, Long idSanPhamChiTiet) {
        // Lấy HoaDonChiTiet theo idHoaDon và idSanPhamChiTiet
        HoaDonChiTiet hdct = hoaDonChiTietRepo.findByHoaDon_IdAndSanPhamChiTiet_Id(idHoaDon, idSanPhamChiTiet);

        // Kiểm tra nếu không có bản ghi nào tìm thấy
        if (hdct != null) {
            // Kiểm tra nếu số lượng hiện tại bằng 0
            if (hdct.getSoLuong() <= 1) {
                throw new RuntimeException("Số lượng không thể nhỏ hơn 1");
            }

            // Giảm số lượng đi 1
            hdct.setSoLuong(hdct.getSoLuong() - 1);

            // Cập nhật lại thành tiền
            BigDecimal gia = hdct.getGia();
            hdct.setThanhTien(gia.multiply(BigDecimal.valueOf(hdct.getSoLuong())));

            // Lưu lại bản ghi HoaDonChiTiet đã cập nhật
            hoaDonChiTietRepo.save(hdct);
        } else {
            throw new RuntimeException("Không tìm thấy hóa đơn chi tiết với ID sản phẩm chi tiết.");
        }
    }


    @Override
    public void truSoLuongSanPham(Long idHD) {
        List<HoaDonChiTiet> listHDCT = hoaDonChiTietRepo.findByHoaDon_Id(idHD);
        for (HoaDonChiTiet hdct : listHDCT) {
            SanPhamChiTiet spct = hdct.getSanPhamChiTiet();
            int soLuongTon = spct.getSo_luong(); // Số lượng hiện tại trong kho
            int soLuongBan = hdct.getSoLuong();    // Số lượng trong hóa đơn chi tiết

            if (soLuongTon >= soLuongBan) {
                spct.setSo_luong(soLuongTon - soLuongBan); // Trừ số lượng
                spctRepo.save(spct);
            } else {
                throw new RuntimeException("Số lượng tồn kho không đủ cho sản phẩm: " + spct.getSanPham().getTen());
            }
        }
    }


    @Override
    public List<HoaDon> getHoaDonByIdKH(Integer idKH) {
        return hoaDonRepo.findByKhachHang_Id(idKH);
    }

    //khoi
    @Override
    public void saveHoaDonChiTiet(HoaDonChiTiet hoaDonChiTiet) {
        hoaDonChiTietRepo.save(hoaDonChiTiet); // Lưu chi tiết hóa đơn vào DB
    }

    //khoi
    @Override
    public String generateOrderCode() {
        // Lấy số lượng hóa đơn hiện tại
        Long count = hoaDonRepo.count(); // Số lượng hóa đơn trong DB
        // Tạo mã hóa đơn với tiền tố "HD" và số thứ tự
        return String.format("HD%03d", count + 1); // VD: HD001, HD002
    }

    @Override
    public Page<HoaDon> getAllHoaDonByLoaiHoaDon(boolean isOnline, Pageable pageable) {
        return hoaDonRepo.findAllByLoaiHoaDon(isOnline, pageable);
    }

    @Override
    public Page<HoaDon> getHoaDonByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return hoaDonRepo.findHoaDonByDateRange(startDate, endDate, pageable);
    }


//    @Override
//    public void getIdSPCT() {
//        List<ListSpNewInHoaDonResponse> listSpInHoaDon = hoaDonChiTietRepo.findSanPhamInHoaDon();
//        for (ListSpNewInHoaDonResponse item : listSpInHoaDon) {
//            Long idSPCT = item.getIdSPCT();
//        }
//    }

}
