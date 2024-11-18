package com.example.datn.service.Implements;

import com.example.datn.dto.response.LichSuThanhToanResponse;
import com.example.datn.dto.response.ListSanPhamInHoaDonChiTietResponse;
import com.example.datn.dto.response.PggInHoaDonResponse;
import com.example.datn.dto.response.ListSpNewInHoaDonResponse;
import com.example.datn.entity.HoaDon;
import com.example.datn.entity.HoaDonChiTiet;
import com.example.datn.entity.HoaDonOff;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.repository.BanOffRepo.HoaDonOffRepo;
import com.example.datn.repository.HoaDonChiTietRepo;
import com.example.datn.repository.HoaDonRepo;
import com.example.datn.repository.SPCTRepo;
import com.example.datn.service.HoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HoaDonServiceImpl implements HoaDonService {
    private final HoaDonRepo hoaDonRepo;
    private final HoaDonChiTietRepo hoaDonChiTietRepo;
    private final SPCTRepo spctRepo;
    private final HoaDonOffRepo hoaDonOffRepo;


    @Override
    public Page<HoaDon> findAll(Pageable pageable) {
        return hoaDonRepo.findAll(pageable);
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

//    @Override
//    public void getIdSPCT() {
//        List<ListSpNewInHoaDonResponse> listSpInHoaDon = hoaDonChiTietRepo.findSanPhamInHoaDon();
//        for (ListSpNewInHoaDonResponse item : listSpInHoaDon) {
//            Long idSPCT = item.getIdSPCT();
//        }
//    }

}
