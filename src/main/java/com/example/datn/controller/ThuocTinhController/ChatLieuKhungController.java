package com.example.datn.controller.ThuocTinhController;

import com.example.datn.entity.thuoc_tinh.ChatLieuCanh;
import com.example.datn.entity.thuoc_tinh.ChatLieuKhung;
import com.example.datn.entity.thuoc_tinh.ChatLieuKhung;
import com.example.datn.repository.ThuocTinhRepo.ChatLieuKhungRepo;
import com.example.datn.service.thuoc_tinh_service.ChatLieuKhungService;
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
@RequestMapping("/admin/thuoc-tinh/chat-lieu-khung")
public class ChatLieuKhungController {
    @Autowired
    ChatLieuKhungRepo chatLieuKhungRepo; // sửa ở đây(1)

    @Autowired
    ChatLieuKhungService chatLieuKhungService; // sửa ở đây(2)

    @ModelAttribute("listChatLieuKhung") // sửa ở đây(3)
    public List<ChatLieuKhung> listChatLieuKhung() {
        return chatLieuKhungRepo.findAll();
    }

    @GetMapping("/view")
    public String timKiem(@RequestParam(value = "ten_chat_lieu_khung", defaultValue = "") String ten_chat_lieu_khung, // sửa ở đây(4)
                          @RequestParam(value = "trang_thai", defaultValue = "") String trang_thaiStr,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "5") int size,
                          Model model) {
        Boolean trang_thai = null;
        if ("1".equals(trang_thaiStr)) {
            trang_thai = true;
        } else if ("0".equals(trang_thaiStr)) {
            trang_thai = false;
        }
        Page<ChatLieuKhung> searchPage = chatLieuKhungService.search(ten_chat_lieu_khung, trang_thai, PageRequest.of(page, size)); // sửa ở đây(5)
        model.addAttribute("listCLK", searchPage); // sửa ở đây(6)
        model.addAttribute("ten_chat_lieu_khung", ten_chat_lieu_khung);
        model.addAttribute("trang_thai", trang_thaiStr);
        return "admin/thuoc_tinh/chat_lieu_khung"; // sửa ở đây(7)
    }

    @PostMapping("/doi-trang-thai/{id}")
    public ResponseEntity doiTrangThai(@PathVariable("id") Integer id) {
        ChatLieuKhung chatLieuKhung = chatLieuKhungRepo.findById(id).orElse(null); // sửa ở đây(8)
        if (chatLieuKhung != null) {
            if (chatLieuKhung.getTrang_thai()) {
                long countTrue = chatLieuKhungRepo.findAll().stream()
                        .filter(c -> c.getTrang_thai() && !c.getId().equals(id))
                        .count();

                if (countTrue == 0) {
                    return ResponseEntity.badRequest().body("Cần có ít nhất một chất liệu khung hoạt động");
                }
            }
            chatLieuKhung.setTrang_thai(!chatLieuKhung.getTrang_thai());
            chatLieuKhungRepo.save(chatLieuKhung);
            return ResponseEntity.ok("Chất liệu khung thay đổi trạng thái thành công."); // sửa ở đây(9)
        } else {
            return ResponseEntity.badRequest().body("Không có chất liệu khung hiện tại.");
        }
    }

    @PostMapping("/addChatLieuKhung")
    public ResponseEntity<?> themMoi(@RequestParam("ten_chat_lieu_khung") String tenChatLieuKhung) {
        if (tenChatLieuKhung == null || tenChatLieuKhung.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên chất liệu khung không được để trống.");
        }
        Optional<ChatLieuKhung> checkTonTai = chatLieuKhungRepo.findByTen(tenChatLieuKhung.trim());
        if (checkTonTai.isPresent()) {
            return ResponseEntity.badRequest().body("Đã tồn tại chất liệu khung.");
        } else {
            ChatLieuKhung chatLieuKhung = new ChatLieuKhung();
            chatLieuKhung.setTen(tenChatLieuKhung.trim());
            chatLieuKhung.setTrang_thai(true);
            chatLieuKhungRepo.save(chatLieuKhung);
            return ResponseEntity.ok("Chất liệu khung thêm mới thành công.");
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> thongTin(@PathVariable("id") Integer id) {
        Optional<ChatLieuKhung> chatLieuKhung = chatLieuKhungRepo.findById(id);
        if (chatLieuKhung.isPresent()) {
            System.out.println("Found ChatLieuKhung: " + chatLieuKhung.get());
            return ResponseEntity.ok(chatLieuKhung.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tồn tại chất liệu khung.");
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateChatLieuKhung(@RequestParam("id") Integer id, @RequestParam("ten_chat_lieu_khung_update") String tenChatLieuKhung) {
        if (tenChatLieuKhung == null || tenChatLieuKhung.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên chất liệu khung không được để trống.");
        }

        Optional<ChatLieuKhung> checkTonTai = chatLieuKhungRepo.findByTen(tenChatLieuKhung.trim());
        if (checkTonTai.isPresent()) {
            return ResponseEntity.badRequest().body("Đã tồn tại chất liệu khung.");
        }
        Optional<ChatLieuKhung> chatLieuKhungOptional = chatLieuKhungRepo.findById(id);
        if (chatLieuKhungOptional.isPresent()) {
            ChatLieuKhung chatLieuKhung = chatLieuKhungOptional.get();
            chatLieuKhung.setTen(tenChatLieuKhung);
            chatLieuKhungRepo.save(chatLieuKhung);
            return ResponseEntity.ok("Cập nhật chất liệu khung thành công.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy chất liệu khung.");
        }
    }
}
