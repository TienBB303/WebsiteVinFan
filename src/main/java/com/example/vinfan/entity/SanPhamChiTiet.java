package com.example.vinfan.entity;

import com.example.vinfan.entity.thuoc_tinh.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "san_pham_chi_tiet")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SanPhamChiTiet {
    //    CREATE TABLE san_pham_chi_tiet (
//    id BIGINT PRIMARY KEY IDENTITY(1, 1),
//    id_mau_sac INT FOREIGN KEY REFERENCES mau_sac(id),
//    id_san_pham BIGINT FOREIGN KEY REFERENCES san_pham(id),
//    id_chat_lieu_canh INT FOREIGN KEY REFERENCES chat_lieu_canh(id),
//    id_nut_bam INT FOREIGN KEY REFERENCES nut_bam(id),
//    id_duong_kinh_canh INT FOREIGN KEY REFERENCES duong_kinh_canh(id),
//    id_chat_lieu_khung INT FOREIGN KEY REFERENCES chat_lieu_khung(id),
//    id_cong_suat INT FOREIGN KEY REFERENCES cong_suat(id),
//    id_dieu_khien_tu_xa INT FOREIGN KEY REFERENCES dieu_khien_tu_xa(id),
//    id_hang INT FOREIGN KEY REFERENCES hang(id),
//    id_chieu_cao INT FOREIGN KEY REFERENCES chieu_cao(id),
//    id_de_quat INT FOREIGN KEY REFERENCES de_quat(id),
//    id_che_do_gio INT FOREIGN KEY REFERENCES che_do_gio(id),
//    gia MONEY,
//    gia_nhap MONEY,
//    hinh_anh NVARCHAR(255),
//    so_luong INT,
//	  ngay_tao DATETIME,
//	  nguoi_tao NVARCHAR(255),
//	  ngay_sua DATETIME,
//    nguoi_sua NVARCHAR(255),
//    trang_thai BIT
//);
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

    String hinh_anh;

    Integer so_luong;

    Date ngay_tao;

    String nguoi_tao;

    Date ngay_sua;

    String nguoi_sua;

    Boolean trang_thai;

}
