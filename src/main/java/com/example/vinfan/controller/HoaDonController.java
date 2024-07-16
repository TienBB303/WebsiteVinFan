package com.example.vinfan.controller;

import com.example.vinfan.dto.HoaDonChiTietDTO;
import com.example.vinfan.entity.HoaDon;
import com.example.vinfan.entity.HoaDonChiTiet;
import com.example.vinfan.repository.HoaDonChiTietRepo;
import com.example.vinfan.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/hoa-don")
public class HoaDonController {

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private HoaDonChiTietRepo hoaDonChiTietRepo;

    @GetMapping("index")
    public String index(@RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "3") int size,
                        Model model) {
        Page<HoaDon> list = hoaDonService.findHoaDonAndSortDay(page, size);
        model.addAttribute("list", list);
        return "/admin/hoa_don/index";
    }
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Integer id, Model model) {
        HoaDon hoaDon = hoaDonService.findById(id);
        List<HoaDonChiTietDTO> list = this.hoaDonService.getSanPhamByHoaDonId(id);
        model.addAttribute("listHDCT", list);

        model.addAttribute("hoaDon", hoaDon);
        return "/admin/hoa_don/detail";
    }

//    @GetMapping("/test")
//    public List<HoaDonChiTietDTO> test(){
//        return mapper.convertDTO(hoaDonChiTietRepo.findAll());
//    }
}
