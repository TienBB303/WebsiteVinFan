package com.example.datn.service.Implements;

import com.example.datn.dto.request.AddSPToHoaDonChiTietRequest;
import com.example.datn.dto.response.ListSanPhamInHoaDonChiTietResponse;
import com.example.datn.dto.response.HoaDonResponse;
import com.example.datn.dto.response.ListSpNewInHoaDonResponse;
import com.example.datn.entity.HoaDon;
import com.example.datn.entity.HoaDonChiTiet;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.repository.HoaDonChiTietRepo;
import com.example.datn.repository.HoaDonRepo;
import com.example.datn.repository.SPCTRepo;
import com.example.datn.service.HoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HoaDonServiceImpl implements HoaDonService {
    private final HoaDonRepo hoaDonRepo;
    private final HoaDonChiTietRepo hoaDonChiTietRepo;
    private final SPCTRepo spctRepo;


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
    public List<ListSanPhamInHoaDonChiTietResponse> getSanPhamByHoaDonId(Long hoaDonId) {

    public List<HoaDonChiTietResponse> getSanPhamByHoaDonId(Long hoaDonId) {
        return hoaDonChiTietRepo.findSanPhamByHoaDonId(hoaDonId);
    }

    @Override
    public List<ListSpNewInHoaDonResponse> getSanPhamInHoaDon() {
        return hoaDonChiTietRepo.findSanPhamInHoaDon();
    }

    @Override
    public HoaDonResponse getPGGbyHoaDonId(Long hoaDonId) {
        return hoaDonRepo.findPGGByHoaDonId(hoaDonId);
    }

    @Override
    public Optional<HoaDon> findById(Long id) {
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(id);
        return hoaDonOptional;
    }

    @Override
    public Page<HoaDon> searchHoaDon(String query, Pageable pageable) {
        return hoaDonRepo.searchHoaDon(query, pageable);
    }

    @Override
    public Page<HoaDon> getAllHoaDonByTrangThai(Integer trangThai,Pageable pageable) {
        return hoaDonRepo.findAllByTrangThai(trangThai,pageable);
    }

    @Override
    public HoaDonChiTiet addSanPhamToHDCT(AddSPToHoaDonChiTietRequest request) {
        HoaDon hoaDon = hoaDonRepo.findById(request.getHoaDonId())
                .orElseThrow(() -> new RuntimeException("Hoa Don không tồn tại"));
        SanPhamChiTiet sanPhamChiTiet = spctRepo.findById(request.getSanPhamChiTietId())
                .orElseThrow(() -> new RuntimeException("San Pham Chi Tiet không tồn tại"));

        // Tạo và lưu HoaDonChiTiet mới
        HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
        hoaDonChiTiet.setHoaDon(hoaDon);
        hoaDonChiTiet.setSanPhamChiTiet(sanPhamChiTiet);
        hoaDonChiTiet.setSoLuong(request.getSoLuong());
        hoaDonChiTiet.setGia(request.getGia());

        return hoaDonChiTietRepo.save(hoaDonChiTiet);
    }

//    @Override
//    public void getIdSPCT() {
//        List<ListSpNewInHoaDonResponse> listSpInHoaDon = hoaDonChiTietRepo.findSanPhamInHoaDon();
//        for (ListSpNewInHoaDonResponse item : listSpInHoaDon) {
//            Long idSPCT = item.getIdSPCT();
//        }
//    }
=======
    @Override
    public Page<HoaDon> findByNgayTao(LocalDate date, Pageable pageable) {
        return hoaDonRepo.findByNgayTao(date, pageable);
    }

//  TienBB
    @Override
    public List<HoaDon> getAllHoaDon() {
        return hoaDonRepo.findAll();
    }

}
