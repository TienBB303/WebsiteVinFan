//package com.example.datn.dto.request;
//
//import jakarta.validation.constraints.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.math.BigDecimal;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class SPCTUpdateRequest {
//
////    @NotNull(message = "ID sản phẩm không được để trống.")
//    private Long id;
//
//    @NotBlank(message = "Tên sản phẩm không được để trống.")
//    private String ten;
//
//    private String mo_ta;
//
//    @NotNull(message = "Giá sản phẩm không được để trống")
//    @DecimalMin(value = "10000", message = "Giá sản phẩm phải lớn hơn hoặc bằng 10.000.")
//    @DecimalMax(value = "20000000", message = "Giá sản phẩm phải Không được quá 20.000.000")
//    private BigDecimal gia;
//
//    @NotNull(message = "Số lượng không được để trống.")
//    @Min(value = 0, message = "Số lượng không thể nhỏ hơn 0.")
//    @Max(value = 500, message = "Số lượng không được vượt quá 500.")
//    private Integer so_luong;
//
//    @NotNull(message = "Bạn phải chọn màu sắc.")
//    private Integer mauSacId;
//
//    @NotNull(message = "Bạn phải chọn công suất.")
//    private Integer congSuatId;
//
//    private MultipartFile hinhAnhHinhAnh1;
//}
