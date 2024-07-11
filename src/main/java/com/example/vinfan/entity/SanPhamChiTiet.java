package com.example.vinfan.entity;

import com.example.vinfan.entity.thuoc_tinh.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "san_pham_chi_tiet")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SanPhamChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "id_mau_sac")
    MauSac mauSac;

    @ManyToOne
    @JoinColumn(name = "id_san_pham")
    SanPham sanPham;

    @ManyToOne
    @JoinColumn(name = "id_chat_lieu_canh")
    ChatLieuCanh chatLieuCanh;

    @ManyToOne
    @JoinColumn(name = "id_nut_bam")
    NutBam nutBam;

    @ManyToOne
    @JoinColumn(name = "id_duong_kinh_canh")
    DuongKinhCanh duongKinhCanh;

    @ManyToOne
    @JoinColumn(name = "id_chat_lieu_khung")
    ChatLieuKhung chatLieuKhung;

    @ManyToOne
    @JoinColumn(name = "id_cong_suat")
    CongSuat congSuat;

    @ManyToOne
    @JoinColumn(name = "id_dieu_khien_tu_xa")
    DieuKhienTuXa dieuKhienTuXa;

    @ManyToOne
    @JoinColumn(name = "id_hang")
    Hang hang;

    @ManyToOne
    @JoinColumn(name = "id_chieu_cao")
    ChieuCao chieuCao;

    @ManyToOne
    @JoinColumn(name = "id_de_quat")
    DeQuat deQuat;

    @ManyToOne
    @JoinColumn(name = "id_che_do_gio")
    CheDoGio cheDoGio;

    BigDecimal gia;

    BigDecimal gia_nhap;

    @Transient
    MultipartFile hinhAnhFile;

    String hinh_anh;


    Integer so_luong;

    Date ngay_tao;

    String nguoi_tao;

    Date ngay_sua;

    String nguoi_sua;

    Boolean trang_thai;

}
