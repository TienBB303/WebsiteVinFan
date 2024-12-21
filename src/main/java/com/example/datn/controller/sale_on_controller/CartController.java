package com.example.datn.controller.sale_on_controller;

import com.example.datn.dto.request.sale_on_request.CartItemRequest;
import com.example.datn.dto.request.sale_on_request.CreateHoaDonRequest;
import com.example.datn.entity.*;
import com.example.datn.entity.phieu_giam.PhieuGiam;
import com.example.datn.entity.sale_on.CartItem;
import com.example.datn.repository.DiaChiRepository;
import com.example.datn.repository.KhachHangRepo;
import com.example.datn.repository.LichSuHoaDonRepo;
import com.example.datn.repository.SPCTRepo;
import com.example.datn.service.HoaDonService;
import com.example.datn.service.SanPhamService;
import com.example.datn.service.TrangThaiHoaDonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/cart")
public class CartController {
    private static final String CART_COOKIE_NAME = "cart";
    private static final int COOKIE_MAX_AGE = 7 * 24 * 60 * 60; // Lưu trong 7 ngày
    @Autowired
    private HoaDonService hoaDonService;
    @Autowired
    private SPCTRepo spctRepo;

    @Autowired
    private LichSuHoaDonRepo lichSuHoaDonRepo;

    @Autowired
    private TrangThaiHoaDonService trangThaiHoaDonService;

    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    private KhachHangRepo khachHangRepo;
    @Autowired
    private DiaChiRepository diaChiRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam BigDecimal price,
                            @RequestParam(required = false) BigDecimal discountedPrice, // Giá giảm (nếu có)
                            @RequestParam int quantity,
                            HttpSession session) {
        // Lấy danh sách giỏ hàng từ session m
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }

        // Kiểm tra sản phẩm đã tồn tại trong giỏ hàng chưa
        Optional<CartItem> existingItem = cart.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            // Nếu sản phẩm đã tồn tại trong giỏ, tăng số lượng
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            // Nếu sản phẩm chưa tồn tại trong giỏ, lấy thêm thông tin từ DB
            SanPhamChiTiet productDetails = spctRepo.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm chi tiết"));

            // Sử dụng giá giảm nếu có
            double finalPrice = discountedPrice != null ? discountedPrice.doubleValue() : price.doubleValue();

            // Tạo đối tượng CartItem mới với thông tin đầy đủ
            CartItem newItem = new CartItem(
                    productId,
                    productDetails.getSanPham().getTen(),       // Tên sản phẩm
                    finalPrice,                                // Sử dụng giá sau giảm nếu có
                    quantity,
                    null, // Giá giảm
                    productDetails.getMauSac().getTen(),        // Màu sắc
                    productDetails.getCongSuat().getTen()       // Công suất
            );
            cart.add(newItem);
        }

        // Cập nhật lại giỏ hàng trong session
        session.setAttribute("cart", cart);

        return "redirect:/cart/view";
    }
    // Phương thức hiển thị giỏ hàng
    @GetMapping("/view")
    public String viewCart(HttpSession session, HttpServletRequest request, Model model) {
        List<CartItem> cart = getCartFromSession(session);

        if (cart.isEmpty()) {
            // Nếu giỏ hàng trong session trống, kiểm tra trong cookie
            cart = getCartFromCookie(request);
            if (!cart.isEmpty()) {
                session.setAttribute("cart", cart); // Khôi phục giỏ hàng từ cookie vào session
            }
        }

        // Tạo một Map để lưu trữ số lượng tồn kho cho từng sản phẩm
        Map<Long, Integer> stockMap = new HashMap<>();

        // Lấy số lượng tồn kho từ SanPhamChiTiet và lưu vào Map
        for (CartItem item : cart) {
            Optional<SanPhamChiTiet> sanPhamChiTietOpt = spctRepo.findById(item.getProductId());

            if (sanPhamChiTietOpt.isPresent()) {
                SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietOpt.get();
                // Thêm số lượng tồn kho vào Map với key là productId
                stockMap.put(item.getProductId(), sanPhamChiTiet.getSo_luong());
            }
        }

        double totalPrice = cart.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
        model.addAttribute("cartItems", cart);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("stockMap", stockMap);  // Thêm stockMap vào model

        return "admin/website/viewCart";
    }
    // Lấy giỏ hàng từ session
    private List<CartItem> getCartFromSession(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }
        return cart;
    }

    // Lưu giỏ hàng vào cookie
    private void saveCartToCookie(List<CartItem> cart, HttpServletResponse response) {
        try {
            // Chuyển danh sách giỏ hàng sang JSON
            String cartJson = objectMapper.writeValueAsString(cart);

            // Mã hóa Base64 để tránh ký tự không hợp lệ
            String encodedCart = Base64.getEncoder().encodeToString(cartJson.getBytes("UTF-8"));

            // Tạo cookie và lưu với giá trị đã mã hóa
            Cookie cartCookie = new Cookie(CART_COOKIE_NAME, encodedCart);
            cartCookie.setMaxAge(COOKIE_MAX_AGE); // Thời gian sống của cookie
            cartCookie.setPath("/");
            response.addCookie(cartCookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<CartItem> getCartFromCookie(HttpServletRequest request) {
        List<CartItem> cart = new ArrayList<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (CART_COOKIE_NAME.equals(cookie.getName())) {
                    try {
                        // Giải mã giá trị Base64 từ cookie
                        String decodedCart = new String(Base64.getDecoder().decode(cookie.getValue()), "UTF-8");

                        // Chuyển JSON đã giải mã thành danh sách CartItem
                        cart = Arrays.asList(objectMapper.readValue(decodedCart, CartItem[].class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return cart;
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<String> updateCartItem(@RequestBody Map<String, Object> payload, HttpSession session) {
        Long productId = Long.valueOf(payload.get("productId").toString());
        int quantity = Integer.parseInt(payload.get("quantity").toString());

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        if (cart != null) {
            for (CartItem item : cart) {
                if (item.getProductId().equals(productId)) {
                    item.setQuantity(quantity);
                    session.setAttribute("cart", cart);  // Lưu lại giỏ hàng vào session
                    return ResponseEntity.ok("Cập nhật số lượng thành công");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không tìm thấy sản phẩm");
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeCartItem(@RequestBody CartItemRequest request, HttpSession session, HttpServletResponse response) {
        List<CartItem> cart = getCartFromSession(session);

        if (cart != null && !cart.isEmpty()) {
            // Xóa sản phẩm theo productId
            cart.removeIf(item -> item.getProductId().equals(request.getProductId()));

            if (cart.isEmpty()) {
                // Xóa session và cookie nếu giỏ hàng trống
                session.removeAttribute("cart");  // Xóa session
                Cookie cookie = new Cookie("cart", "");
                cookie.setMaxAge(0);  // Xóa cookie
                cookie.setPath("/");
                response.addCookie(cookie);  // Gửi cookie rỗng về client
            } else {
                // Nếu vẫn còn sản phẩm, cập nhật lại session và cookie
                session.setAttribute("cart", cart);
                saveCartToCookie(cart, response);
            }
        }

        return ResponseEntity.ok(new ApiResponse("Item removed successfully"));
    }

    @GetMapping("/orderinfor")
    public String orderInfor(HttpSession session, Model model) {
        // Lấy danh sách sản phẩm từ session
        List<CartItem> cartItems = getCartFromSession(session);

        KhachHang khachHang = khachHangRepo.profileKhachHang();
        model.addAttribute("khachHang",khachHang);

        List<DiaChi> diaChi =  diaChiRepository.findByKhachHangId(khachHang.getId());
        for ( DiaChi x: diaChi) {
            if (x.getTrangThai() == true){
                model.addAttribute("diaChiMacDinh",diaChi);
            }
        }


        // Tính tổng tiền
        double totalPrice = cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        // Đưa thông tin giỏ hàng và tổng tiền vào model để Thymeleaf có thể truy cập
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);

        return "admin/website/orderInfor";
    }

    @GetMapping("/check-out")
    public String checkOut(HttpSession session, Model model) {
        // Lấy danh sách sản phẩm từ session
        List<CartItem> cartItems = getCartFromSession(session);

        // Tính tổng tiền
        double totalPrice = cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        // Đưa thông tin giỏ hàng và tổng tiền vào model để Thymeleaf có thể truy cập
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);

        return "admin/website/checkOut";
    }

    @Transactional
    @PostMapping("/process-payment")
    public String processPayment(@ModelAttribute CreateHoaDonRequest request, HttpSession session, Model model) {
        // Lấy giỏ hàng từ session
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cart");
        if (cartItems == null || cartItems.isEmpty()) {
            return "redirect:/cart/orderinfor?error=CartIsEmpty";
        }
        // Lấy thông tin khách hàng từ tài khoản đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Lấy email đăng nhập
        KhachHang khachHang = khachHangRepo.findByEmail(email).orElse(null);
        if (khachHang == null) {
            return "redirect:/login?error=UserNotLoggedIn"; // Nếu không tìm thấy khách hàng
        }
        // Tạo hóa đơn
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMa(hoaDonService.generateOrderCode());
        hoaDon.setTenNguoiNhan(request.getName());
        hoaDon.setSdtNguoiNhan(request.getPhone());

        // Ghép địa chỉ
        String diaChi = request.getAddress();
        if (request.getDistrict() != null && !request.getDistrict().isEmpty()) {
            diaChi += ", " + request.getDistrict();
        }
        if (request.getProvince() != null && !request.getProvince().isEmpty()) {
            diaChi += ", " + request.getProvince();
        }
        hoaDon.setDiaChi(diaChi);
        hoaDon.setNgayTao(LocalDate.now());
        hoaDon.setTrangThai(1); // Trạng thái chờ xác nhận
        hoaDon.setLoaiHoaDon(false); // Mặc định là false (tương đương 0)

        // Kiểm tra và gán giá trị cho ghi chú
        if (request.getNote() == null || request.getNote().trim().isEmpty()) {
            hoaDon.setGhiChu("Không có ghi chú"); // Gán mặc định
        } else {
            hoaDon.setGhiChu(request.getNote()); // Lưu ghi chú từ request
        }

        // Fake data bổ sung
        hoaDon.setTongTienSauGiamGia(BigDecimal.valueOf(0));
        hoaDon.setNgaySua(LocalDate.now());
        hoaDon.setNguoiTao("system");
        hoaDon.setNguoiSua("system");
        hoaDon.setKhachHang(khachHang);
//        hoaDon.setNhanVien(entityManager.find(NhanVien.class, 1L));
        hoaDon.setPhieuGiamGia(entityManager.find(PhieuGiam.class, 1L));
//        hoaDon.setHinhThucThanhToan(entityManager.find(HinhThucThanhToan.class, 1L));

        // Phí vận chuyển
        BigDecimal shippingFee = BigDecimal.valueOf(30000);
        BigDecimal total = BigDecimal.ZERO;

        // Lưu hóa đơn trước
        hoaDonService.save(hoaDon);

        // Tạo chi tiết hóa đơn
        for (CartItem item : cartItems) {
            SanPhamChiTiet sanPhamChiTiet = entityManager.find(SanPhamChiTiet.class, item.getProductId());
            if (sanPhamChiTiet == null) {
                return "redirect:/cart/orderinfor?error=ProductNotFound";
            }
            HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
            hoaDonChiTiet.setHoaDon(hoaDon);
            hoaDonChiTiet.setSanPhamChiTiet(sanPhamChiTiet);
            hoaDonChiTiet.setSoLuong(item.getQuantity());
            hoaDonChiTiet.setGia(sanPhamChiTiet.getGia());
            hoaDonChiTiet.setThanhTien(sanPhamChiTiet.getGia().multiply(BigDecimal.valueOf(item.getQuantity())));
            hoaDonChiTiet.setTrangThai(1);
            total = total.add(hoaDonChiTiet.getThanhTien());

            hoaDonService.saveHoaDonChiTiet(hoaDonChiTiet);
        }
        //lịch sử hóa đơn
        LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
        lichSuHoaDon.setHoaDon(hoaDon);
        lichSuHoaDon.setTrangThai(trangThaiHoaDonService.getTrangThaiHoaDonRequest().getChoXacNhan());
        lichSuHoaDon.setNgayTao(LocalDate.now());
        lichSuHoaDonRepo.save(lichSuHoaDon);

        // Cập nhật tổng tiền và phí vận chuyển cho hóa đơn
        total = total.add(shippingFee);
        hoaDon.setTongTien(total);
        hoaDon.setPhiVanChuyen(shippingFee);
        hoaDonService.save(hoaDon);

        // Xóa giỏ hàng khỏi session sau khi thanh toán
        session.removeAttribute("cart");

        // Khởi tạo giỏ hàng trống mới trong trường hợp người dùng quay lại
        session.setAttribute("cart", new ArrayList<CartItem>());


        // Thêm hóa đơn vào model để gửi đến view
        model.addAttribute("hoaDon", hoaDon);

        return "admin/website/orderSuccess";
    }
    @PostMapping("/check-stock")
    public ResponseEntity<Map<String, Object>> checkStock(@RequestBody List<CartItemRequest> cartItems) {
        Map<String, Object> response = new HashMap<>();
        boolean allInStock = true;
        StringBuilder message = new StringBuilder();

        for (CartItemRequest item : cartItems) {
            SanPhamChiTiet productDetails = sanPhamService.getProductDetails(item.getProductId());
            if (productDetails != null) {
                int availableStock = productDetails.getSo_luong();
                if (item.getQuantity() > availableStock) {
                    allInStock = false;
                    message.append("Sản phẩm ")
                            .append(productDetails.getSanPham().getTen())
                            .append(" không đủ tồn kho. ");
                }
            } else {
                allInStock = false;
                message.append("Sản phẩm với ID ")
                        .append(item.getProductId())
                        .append(" không tìm thấy. ");
            }
        }

        if (allInStock) {
            response.put("success", true);
            response.put("message", "Tất cả sản phẩm có đủ tồn kho.");
        } else {
            response.put("success", false);
            response.put("message", message.toString());
        }

        return ResponseEntity.ok(response);
    }
}
