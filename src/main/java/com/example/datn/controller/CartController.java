package com.example.datn.controller;

import com.example.datn.entity.*;
import com.example.datn.repository.GioHangChiTietRepo;
import com.example.datn.repository.GioHangRepo;
import com.example.datn.repository.SPCTRepo;
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
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("cart")
public class CartController {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SPCTRepo sanPhamChiTietRepo;
    @Autowired
    private GioHangRepo gioHangRepo;
    @Autowired
    private GioHangChiTietRepo gioHangChiTietRepo;

    private static final String CART_COOKIE_NAME = "cart";
    private static final int COOKIE_MAX_AGE = 7 * 24 * 60 * 60; // Lưu trong 7 ngày

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

}
