package com.example.datn.service.Implements;

import com.example.datn.dto.request.AddSPToHoaDonChiTietRequest;
import com.example.datn.dto.response.*;
import com.example.datn.entity.*;
import com.example.datn.entity.phieu_giam.PhieuGiam;
import com.example.datn.repository.*;
import com.example.datn.repository.phieu_giam_repo.PhieuGiamRepo;
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
import java.util.*;

@Service
@RequiredArgsConstructor
public class HoaDonServiceImpl implements HoaDonService {
    private final HoaDonRepo hoaDonRepo;
    private final HoaDonChiTietRepo hoaDonChiTietRepo;
    private final SPCTRepo spctRepo;
    private final TrangThaiHoaDonService trangThaiHoaDonService;
    private final LichSuHoaDonRepo lichSuHoaDonRepo;
    private final NhanVienRepository nhanVienRepository;
    //    private final PhieuGiamSanPhamRepo phieuGiamSanPhamRepo;
    private final PhieuGiamRepo phieuGiamRepo;

    @Override
    public Page<HoaDon> findAll(Pageable pageable) {
        return hoaDonRepo.findAll(pageable);
    }
@Override
    public Optional<HoaDon> findByIdWithPhieuGiamGia(Long id) {
        return hoaDonRepo.findByIdWithPhieuGiamGia(id);
    }

    @Override
    @Transactional
    public void updateTongTienHoaDon(Long idHoaDon) {
        // Lấy danh sách chi tiết hóa đơn
        List<HoaDonChiTiet> listHDCT = hoaDonChiTietRepo.findByHoaDon_Id(idHoaDon);

        // Tính tổng tiền từ thành tiền của từng dòng
        BigDecimal tongTien = listHDCT.stream()
                .map(HoaDonChiTiet::getThanhTien) // Sử dụng thành tiền đã tính theo giá giảm
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Lưu tổng tiền vào hóa đơn
        HoaDon hoaDon = hoaDonRepo.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với ID: " + idHoaDon));
        hoaDon.setTongTien(tongTien);
        hoaDonRepo.save(hoaDon);
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
    @Transactional
    public List<HoaDonChiTiet> timSanPhamChiTietTheoHoaDon(Long hoaDonId) {
        List<HoaDonChiTiet> hoaDonChiTietList = hoaDonChiTietRepo.findByHoaDon_Id(hoaDonId);

        hoaDonChiTietList.forEach(hdct -> {
            BigDecimal giaGoc = hdct.getSanPhamChiTiet().getGia(); // Lấy giá gốc
            Optional<PhieuGiam> optionalPgg = phieuGiamRepo.findBySanPhamChiTietId(hdct.getSanPhamChiTiet().getId());

            // Tính giá giảm từ phiếu giảm giá
            BigDecimal giaGiam = optionalPgg.map(PhieuGiam::getGiaTriGiam).orElse(BigDecimal.ZERO);

            // Cập nhật giá sau giảm nếu chưa tính
            if (hdct.getGiaSauGiam() == null || hdct.getGiaSauGiam().compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal giaSauGiam = giaGoc.subtract(giaGiam).max(BigDecimal.ZERO);
                hdct.setGiaSauGiam(giaSauGiam); // Cập nhật giá sau giảm
                hdct.setThanhTien(giaSauGiam.multiply(BigDecimal.valueOf(hdct.getSoLuong()))); // Thành tiền dựa trên giá sau giảm
                hoaDonChiTietRepo.save(hdct); // Lưu thay đổi vào DB
            }

            // Cập nhật giá giảm (dùng Transient, không lưu vào DB)
            hdct.setGiaGiam(giaGiam);
        });

        return hoaDonChiTietList;
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
    public Page<HoaDon> searchHoaDon(String query, Boolean loaiHoaDon, LocalDate tuNgay, LocalDate denNgay, Integer trang_thai, Pageable pageable) {
        if (trang_thai == null){
            return hoaDonRepo.searchHoaDonKhongtrangThai(query,loaiHoaDon,tuNgay, denNgay, pageable);
        }
        return hoaDonRepo.searchHoaDon(query,loaiHoaDon,tuNgay, denNgay,trang_thai, pageable);
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

    public HoaDonChiTiet convertToEntity(AddSPToHoaDonChiTietRequest request, HoaDon hoaDon, SanPhamChiTiet sanPhamChiTiet) {
        // Kiểm tra nếu sản phẩm đã có trong chi tiết hóa đơn
        Optional<HoaDonChiTiet> existingDetail = hoaDonChiTietRepo.findByHoaDonAndSanPhamChiTiet(hoaDon, sanPhamChiTiet);

        if (existingDetail.isPresent()) {
            // Nếu sản phẩm đã tồn tại, ném ra ngoại lệ hoặc thông báo lỗi
            throw new IllegalArgumentException("Sản phẩm đã tồn tại trong hóa đơn. Không thể thêm lại.");
        }

        // Nếu sản phẩm chưa tồn tại, tạo chi tiết hóa đơn mới
        HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
        hoaDonChiTiet.setHoaDon(hoaDon);
        hoaDonChiTiet.setSanPhamChiTiet(sanPhamChiTiet);
        hoaDonChiTiet.setGia(request.getGia());
        hoaDonChiTiet.setSoLuong(1); // Số lượng mặc định là 1
        // Tính thành tiền cho sản phẩm
        BigDecimal thanhTien = request.getGia().multiply(BigDecimal.valueOf(1));
        hoaDonChiTiet.setThanhTien(thanhTien);

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
            NhanVien nhanVien = nhanVienRepository.profileNhanVien();
            hoaDon.setNhanVien(nhanVien);
            hoaDon.setNguoiTao(nhanVien.getTen());
            hoaDonRepo.save(hoaDon);

            // Tạo lịch sử cập nhật
            LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
            lichSuHoaDon.setHoaDon(hoaDon);
            lichSuHoaDon.setTrangThai(trangThaiHoaDonService.getTrangThaiHoaDonRequest().getDaXacNhan());
            lichSuHoaDon.setNgayTao(LocalDate.now());
            lichSuHoaDon.setNguoiTao(nhanVien.getTen());

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

            NhanVien nhanVien = nhanVienRepository.profileNhanVien();
            hoaDon.setNhanVien(nhanVien);
            hoaDon.setNguoiTao(nhanVien.getTen());

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
            lichSuHoaDon.setNguoiTao(nhanVien.getTen());

            lichSuHoaDonRepo.save(lichSuHoaDon);

            return true;
        }
        return false;// Khong tim thay hoa don
    }

    @Override
    public boolean hoanThanhHoaDon(long id) {
//        // Tìm kiếm HoaDon dựa trên ID
//        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(id);
//        List<HoaDonChiTiet> listHDCT = hoaDonChiTietRepo.findByHoaDon_Id(id);
//
//        if (hoaDonOptional.isPresent()) {
//            for (HoaDonChiTiet hdct : listHDCT) {
//                SanPhamChiTiet spct = hdct.getSanPhamChiTiet();
//                int soLuongTon = spct.getSo_luong(); // Số lượng hiện tại trong kho
//                int soLuongBan = hdct.getSoLuong();    // Số lượng trong hóa đơn chi tiết
//
//                if (soLuongTon < soLuongBan) {
//                    throw new RuntimeException("Số lượng tồn kho không đủ cho sản phẩm: " + spct.getSanPham().getTen());
//                }
//            }
//            HoaDon hoaDon = hoaDonOptional.get();
//
//            // Cập nhật trạng thái của HoaDon
//            hoaDon.setTrangThai(trangThaiHoaDonService.getTrangThaiHoaDonRequest().getDaGiaoHang());
//            hoaDonRepo.save(hoaDon);
//
//            // Tạo lịch sử cập nhật
//            LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
//            lichSuHoaDon.setHoaDon(hoaDon);
//            lichSuHoaDon.setTrangThai(trangThaiHoaDonService.getTrangThaiHoaDonRequest().getDaGiaoHang());
//            lichSuHoaDon.setNgayTao(LocalDate.now());
//            lichSuHoaDonRepo.save(lichSuHoaDon);
//
//            return true;
//        }
        return false;// Khong tim thay hoa don
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
                BigDecimal gia = hdct.getGiaSauGiam();
                hdct.setThanhTien(gia.multiply(BigDecimal.valueOf(hdct.getSoLuong())));

                // Lưu lại bản ghi HoaDonChiTiet đã cập nhật
                updateTongTienHoaDon(idHoaDon);
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
            BigDecimal gia = hdct.getGiaSauGiam();
            hdct.setThanhTien(gia.multiply(BigDecimal.valueOf(hdct.getSoLuong())));

            // Lưu lại bản ghi HoaDonChiTiet đã cập nhật
            updateTongTienHoaDon(idHoaDon);
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

//  TienBB
    @Override
    public Map<Integer, Long> countHoaDonByTrangThai() {
        Map<Integer, Long> result = hoaDonRepo.countByTrangThai();

        // Loại bỏ những phần tử có khóa null
        result.entrySet().removeIf(entry -> entry.getKey() == null);

        return result;
    }
}
