package com.example.datn.service.Implements;

import com.example.datn.dto.request.AddSPToHoaDonChiTietRequest;
import com.example.datn.dto.response.LichSuThanhToanResponse;
import com.example.datn.dto.response.ListSanPhamInHoaDonChiTietResponse;
import com.example.datn.dto.response.PggInHoaDonResponse;
import com.example.datn.dto.response.ListSpNewInHoaDonResponse;
import com.example.datn.entity.*;
import com.example.datn.repository.BanOffRepo.HoaDonOffRepo;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HoaDonServiceImpl implements HoaDonService {
    private final HoaDonRepo hoaDonRepo;
    private final HoaDonChiTietRepo hoaDonChiTietRepo;
    private final SPCTRepo spctRepo;
    private final TrangThaiHoaDonService trangThaiHoaDonService;
    private final LichSuHoaDonRepo lichSuHoaDonRepo;
    private final HoaDonOffRepo hoaDonOffRepo;


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
//         Kiểm tra nếu sản phẩm đã có trong chi tiết hóa đơn
        Optional<HoaDonChiTiet> existingDetail = hoaDonChiTietRepo.findByHoaDonAndSanPhamChiTiet(hoaDon, sanPhamChiTiet);
//         Số lượng được mua là 1
        int soLuong = 1;

//         Kiểm tra số lượng trong kho
        int soLuongConLai = sanPhamChiTiet.getSo_luong() - soLuong;

        HoaDonChiTiet hoaDonChiTiet;

        if (existingDetail.isPresent()) {

            // Nếu sản phẩm đã có, cộng thêm số lượng
            hoaDonChiTiet = existingDetail.get();
            int newSoLuong = hoaDonChiTiet.getSoLuong() + 1;  // Cộng thêm 1 sản phẩm

            if (soLuongConLai >= 0) {
                sanPhamChiTiet.setSo_luong(soLuongConLai);
                spctRepo.save(sanPhamChiTiet);  // Lưu lại sản phẩm đã cập nhật
            } else {
                // Xử lý trường hợp không đủ số lượng trong kho
                throw new RuntimeException("Số lượng sản phẩm không đủ!");
            }
            hoaDonChiTiet.setSoLuong(newSoLuong);
//             Cập nhật lại thành tiền
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

//             Trừ số lượng sản phẩm trong database
            if (soLuongConLai >= 0) {
                // Cập nhật lại số lượng trong sản phẩm
                sanPhamChiTiet.setSo_luong(soLuongConLai);
                spctRepo.save(sanPhamChiTiet);  // Lưu lại sản phẩm đã cập nhật
            } else {
                // Xử lý trường hợp không đủ số lượng trong kho
                throw new RuntimeException("Số lượng sản phẩm không đủ!");
            }
        }

        // Lưu chi tiết hóa đơn vào cơ sở dữ liệu
        hoaDonChiTietRepo.save(hoaDonChiTiet);
        return hoaDonChiTiet;
    }

    @Override
    public boolean xacNhanHoaDon(long id) {
        // Tìm kiếm HoaDon dựa trên ID
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(id);

        if (hoaDonOptional.isPresent()) {
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
            for (HoaDonChiTiet hoaDonChiTiet: hoaDon.getHoaDonChiTietList()) {
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
    @Transactional
    public void updateTongTienHoaDon() {
        hoaDonRepo.updateTongTienHoaDon();
    }

    @Override
    public boolean updateQuantity(Long itemId, int quantity) {
        Optional<HoaDonChiTiet> hoaDonChiTiet = hoaDonChiTietRepo.findById(itemId);

        if (hoaDonChiTiet.isPresent()) {
            HoaDonChiTiet item = hoaDonChiTiet.get();
            item.setSoLuong(quantity);
            hoaDonChiTietRepo.save(item);
            return true;
        }
        return false;
    }


//    @Override
//    @Transactional
//    public void updateSoLuong(Long id, Integer soLuong) {
//        hoaDonChiTietRepo.updateSoLuong(soLuong, id);
//    }


    @Override
    public List<HoaDonOff> getAllHoaDonOff() {
        return hoaDonOffRepo.findAll();
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
