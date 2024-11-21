package com.example.datn.controller;

import com.example.datn.dto.request.CreateHoaDonRequest;
import com.example.datn.entity.*;
import com.example.datn.service.HoaDonService;
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

    @PersistenceContext
    private EntityManager entityManager;

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/add")
    public String addToCart(CartItemRequest cartItemRequest, HttpSession session) {
        // Lấy danh sách giỏ hàng từ session
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }

        // Kiểm tra xem sản phẩm có trong giỏ hàng chưa
        Optional<CartItem> existingItem = cart.stream()
                .filter(item -> item.getProductId().equals(cartItemRequest.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Nếu đã có sản phẩm trong giỏ, tăng số lượng
            existingItem.get().setQuantity(existingItem.get().getQuantity() + cartItemRequest.getQuantity());
        } else {
            // Nếu chưa có, thêm mới sản phẩm với giá đã giảm (nếu có)
            BigDecimal price = cartItemRequest.getDiscountedPrice() != null
                    ? cartItemRequest.getDiscountedPrice()
                    : cartItemRequest.getPrice();

            CartItem newItem = new CartItem(
                    cartItemRequest.getProductId(),
                    cartItemRequest.getName(),
                    price.doubleValue(),  // Chuyển đổi sang double để xử lý
                    cartItemRequest.getQuantity(),
                    cartItemRequest.getDiscountedPrice() // Lưu giá đã giảm
            );
            cart.add(newItem);
        }

        // Cập nhật lại session
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

        double totalPrice = cart.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
        model.addAttribute("cartItems", cart);
        model.addAttribute("totalPrice", totalPrice);

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
    public String processPayment(@ModelAttribute CreateHoaDonRequest request, HttpSession session) {
        // Lấy giỏ hàng từ session
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cart");
        if (cartItems == null || cartItems.isEmpty()) {
            return "redirect:/cart/orderinfor?error=CartIsEmpty";
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
        hoaDon.setLoaiHoaDon(request.getPaymentMethod().equalsIgnoreCase("COD"));

        // **Fake data bổ sung**
        hoaDon.setTongTienSauGiamGia(BigDecimal.valueOf(0)); // Giá trị mặc định nếu không có giảm giá
        hoaDon.setGhiChu("Fake note"); // Ghi chú mặc định
        hoaDon.setNgaySua(LocalDate.now()); // Ngày sửa mặc định là ngày hiện tại
        hoaDon.setNguoiTao("system"); // Người tạo mặc định
        hoaDon.setNguoiSua("system"); // Người sửa mặc định

        // **Fake ID cho các quan hệ ManyToOne**
        hoaDon.setKhachHang(entityManager.find(KhachHang.class, 1L)); // ID giả lập cho Khách Hàng
        hoaDon.setNhanVien(entityManager.find(NhanVien.class, 1L)); // ID giả lập cho Nhân Viên
        hoaDon.setPhieuGiamGia(entityManager.find(PhieuGiam.class, 1L)); // ID giả lập cho Phiếu Giảm Giá
        hoaDon.setHinhThucThanhToan(entityManager.find(HinhThucThanhToan.class, 1L)); // ID giả lập cho Hình Thức Thanh Toán

        // Phí vận chuyển
        BigDecimal shippingFee = BigDecimal.valueOf(30000); // 30,000 VND
        BigDecimal total = BigDecimal.ZERO;

        // Lưu hóa đơn trước
        hoaDonService.save(hoaDon);
        System.out.println("HoaDon saved with ID: " + hoaDon.getId());

        // Tạo chi tiết hóa đơn
        for (CartItem item : cartItems) {
            // Tìm sản phẩm chi tiết
            SanPhamChiTiet sanPhamChiTiet = entityManager.find(SanPhamChiTiet.class, item.getProductId());
            if (sanPhamChiTiet == null) {
                return "redirect:/cart/orderinfor?error=ProductNotFound";
            }

            // Tạo chi tiết hóa đơn
            HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
            hoaDonChiTiet.setHoaDon(hoaDon);
            hoaDonChiTiet.setSanPhamChiTiet(sanPhamChiTiet);
            hoaDonChiTiet.setSoLuong(item.getQuantity());
            hoaDonChiTiet.setGia(sanPhamChiTiet.getGia());
            hoaDonChiTiet.setThanhTien(sanPhamChiTiet.getGia().multiply(BigDecimal.valueOf(item.getQuantity())));
            hoaDonChiTiet.setTrangThai(1); // Trạng thái mặc định

            // Cộng dồn tổng tiền
            total = total.add(hoaDonChiTiet.getThanhTien());

            // Lưu chi tiết hóa đơn
            hoaDonService.saveHoaDonChiTiet(hoaDonChiTiet);
        }

        // Cập nhật tổng tiền và phí vận chuyển cho hóa đơn
        total = total.add(shippingFee);
        hoaDon.setTongTien(total);
        hoaDon.setPhiVanChuyen(shippingFee);
        hoaDonService.save(hoaDon);

        // Xóa giỏ hàng khỏi session
        session.removeAttribute("cart");

        return "redirect:/cart/orderinfor?success=OrderPlaced";
    }

}
