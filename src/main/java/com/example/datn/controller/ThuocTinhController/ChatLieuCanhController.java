package com.example.datn.controller.ThuocTinhController;

import com.example.datn.entity.NhanVien;
import com.example.datn.entity.thuoc_tinh.ChatLieuCanh;
import com.example.datn.entity.thuoc_tinh.CheDoGio;
import com.example.datn.repository.NhanVienRepository;
import com.example.datn.repository.ThuocTinhRepo.ChatLieuCanhRepo;
import com.example.datn.service.thuoc_tinh_service.ChatLieuCanhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/thuoc-tinh/chat-lieu-canh")
public class ChatLieuCanhController {
    @Autowired
    ChatLieuCanhRepo chatLieuCanhRepo;

    @Autowired
    ChatLieuCanhService chatLieuCanhService;

    @Autowired
    NhanVienRepository nhanVienRepository;

    @ModelAttribute("listChatLieuCanh")
    public List<ChatLieuCanh> listChatLieuCanh() {
        return chatLieuCanhRepo.findAll();
    }

    @GetMapping("/view")
    public String timKiem(@RequestParam(value = "ten_chat_lieu_canh", defaultValue = "") String ten_chat_lieu_canh,
                          @RequestParam(value = "trang_thai", defaultValue = "") Boolean trang_thai,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "5") int size,
                          Model model) {
        Page<ChatLieuCanh> searchPage = chatLieuCanhService.search(ten_chat_lieu_canh.trim(), trang_thai, PageRequest.of(page, size));
        if (searchPage.isEmpty() && page < 0) {
            searchPage = chatLieuCanhService.search(ten_chat_lieu_canh.trim(), trang_thai, PageRequest.of(0, size));
        }
        model.addAttribute("listCLC", searchPage);
        model.addAttribute("ten_chat_lieu_canh", ten_chat_lieu_canh);
        model.addAttribute("trang_thai", trang_thai != null ? trang_thai : "");
        NhanVien nv = nhanVienRepository.profileNhanVien();
        model.addAttribute("nhanVienInfo", nv);
        return "admin/thuoc_tinh/chat_lieu_canh";
    }

    @PostMapping("/doi-trang-thai/{id}")
    public ResponseEntity doiTrangThai(@PathVariable("id") Integer id) {
        ChatLieuCanh chatLieuCanh = chatLieuCanhRepo.findById(id).orElse(null);
        if (chatLieuCanh != null) {
            if (chatLieuCanh.getTrang_thai()) {
                long countTrue = chatLieuCanhRepo.findAll().stream()
                        .filter(c -> c.getTrang_thai() && !c.getId().equals(id))
                        .count();

                if (countTrue == 0) {
                    return ResponseEntity.badRequest().body("Cần có ít nhất một chất liệu cánh hoạt động");
                }
            }
            chatLieuCanh.setTrang_thai(!chatLieuCanh.getTrang_thai());
            chatLieuCanhRepo.save(chatLieuCanh);
            return ResponseEntity.ok("Chất liệu cánh thay đổi trạng thái thành công.");
        } else {
            return ResponseEntity.badRequest().body("Không có chất liệu cánh hiện tại.");
        }
    }

    @PostMapping("/addChatLieuCanh")
    public ResponseEntity<?> themMoi(@RequestParam("ten_chat_lieu_canh") String tenChatLieuCanh) {
        if (tenChatLieuCanh == null || tenChatLieuCanh.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên chất liệu cánh không được để trống.");
        }
        Optional<ChatLieuCanh> checkTonTai = chatLieuCanhRepo.findByTen(tenChatLieuCanh.trim());
        if (checkTonTai.isPresent()) {
            return ResponseEntity.badRequest().body("Đã tồn tại chất liệu cánh.");
        } else {
            ChatLieuCanh chatLieuCanh = new ChatLieuCanh();
            chatLieuCanh.setTen(tenChatLieuCanh.trim());
            chatLieuCanh.setTrang_thai(true);
            chatLieuCanhRepo.save(chatLieuCanh);
            return ResponseEntity.ok("Chất liệu cánh thêm mới thành công.");
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> thongTin(@PathVariable("id") Integer id) {
        Optional<ChatLieuCanh> chatLieuCanh = chatLieuCanhRepo.findById(id);
        if (chatLieuCanh.isPresent()) {
            System.out.println("Found ChatLieuCanh: " + chatLieuCanh.get());
            return ResponseEntity.ok(chatLieuCanh.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tồn tại chất liệu cánh.");
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateChatLieuCanh(@RequestParam("id") Integer id, @RequestParam("ten_chat_lieu_canh_update") String tenChatLieuCanh) {
        if (tenChatLieuCanh == null || tenChatLieuCanh.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên chất liệu cánh không được để trống.");
        }

        Optional<ChatLieuCanh> checkTonTai = chatLieuCanhRepo.findByTen(tenChatLieuCanh.trim());
        if (checkTonTai.isPresent()) {
            return ResponseEntity.badRequest().body("Đã tồn tại chất liệu cánh.");
        }
        Optional<ChatLieuCanh> chatLieuCanhOptional = chatLieuCanhRepo.findById(id);
        if (chatLieuCanhOptional.isPresent()) {
            ChatLieuCanh chatLieuCanh = chatLieuCanhOptional.get();
            chatLieuCanh.setTen(tenChatLieuCanh.trim());
            chatLieuCanhRepo.save(chatLieuCanh);
            return ResponseEntity.ok("Cập nhật chất liệu cánh thành công.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy chất liệu cánh.");
        }
    }
}
