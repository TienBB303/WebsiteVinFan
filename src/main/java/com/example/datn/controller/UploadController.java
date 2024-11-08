package com.example.datn.controller;


import com.example.datn.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/hinh-anh")
public class UploadController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("/form-anh")
    public String formanh() {
        return "/admin/upload";
    }


    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {
        try {
            Map uploadResult = cloudinaryService.upload(file);
            model.addAttribute("url", uploadResult.get("url"));  // Thêm URL ảnh vào model
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi xảy ra khi upload hình ảnh");
        }
        return "/admin/upload";  // Trả về cùng một trang để hiển thị ảnh
    }

}


