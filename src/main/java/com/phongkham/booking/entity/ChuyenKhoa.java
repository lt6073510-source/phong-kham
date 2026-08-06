package com.phongkham.booking.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chuyenkhoa")
public class ChuyenKhoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_chuyen_khoa_cu", nullable = false, unique = true)
    private String tenChuyenKhoa;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "hinh_anh")
    private String hinhAnh;

    // ==========================================
    // LIÊN KẾT BÁC SĨ (BỔ SUNG ĐỂ SỬA LỖI THYMELEAF)
    // ==========================================
    @OneToMany(mappedBy = "chuyenKhoa", fetch = FetchType.LAZY)
    private List<BacSi> danhSachBacSi = new ArrayList<>();

    // ==========================================
    // CÁC THUỘC TÍNH BỔ SUNG (ĐÃ CHUẨN HÓA CAMELCASE)
    // ==========================================
    
    @Column(name = "ma_khoa")
    private String maKhoa;

    @Column(name = "ten_chuyen_khoa")
    private String ten_chuyen_khoa;

    @Column(name = "phu_de_banner")
    private String phu_de_banner;

    @Column(name = "doan_gioi_thieu", columnDefinition = "TEXT")
    private String doan_gioi_thieu;

    @Column(name = "tieu_de_hang_muc")
    private String tieu_de_hang_muc;

    @Column(name = "loi_ket", columnDefinition = "TEXT")
    private String loi_ket;

    @ElementCollection
    @CollectionTable(name = "chuyen_khoa_hang_muc", joinColumns = @JoinColumn(name = "chuyen_khoa_id"))
    private List<HangMuc> danh_sach_hang_muc = new ArrayList<>();

    // --- Constructor không đối số ---
    public ChuyenKhoa() {}

    // --- Constructor có đối số cũ ---
    public ChuyenKhoa(String tenChuyenKhoa, String moTa, String hinhAnh) {
        this.tenChuyenKhoa = tenChuyenKhoa;
        this.moTa = moTa;
        this.hinhAnh = hinhAnh;
    }

    // --- Getter & Setter cho danhSachBacSi (SỬA LỖI THYMELEAF) ---
    public List<BacSi> getDanhSachBacSi() {
        return danhSachBacSi;
    }

    public void setDanhSachBacSi(List<BacSi> danhSachBacSi) {
        this.danhSachBacSi = danhSachBacSi;
    }

    // --- Getter & Setter cũ ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTenChuyenKhoa() { return tenChuyenKhoa; }
    public void setTenChuyenKhoa(String tenChuyenKhoa) { this.tenChuyenKhoa = tenChuyenKhoa; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

    // --- Getter & Setter chuẩn hóa ---
    public String getMaKhoa() { return maKhoa; }
    public void setMaKhoa(String maKhoa) { this.maKhoa = maKhoa; }

    // Giữ thêm alias setter này để không bị lỗi file DataInitializer cũ (nếu có dùng setMa_khoa)
    public void setMa_khoa(String ma_khoa) { this.maKhoa = ma_khoa; }
    public String getMa_khoa() { return maKhoa; }

    public String getTen_chuyen_khoa() { return ten_chuyen_khoa; }
    public void setTen_chuyen_khoa(String ten_chuyen_khoa) { this.ten_chuyen_khoa = ten_chuyen_khoa; }

    public String getPhu_de_banner() { return phu_de_banner; }
    public void setPhu_de_banner(String phu_de_banner) { this.phu_de_banner = phu_de_banner; }

    public String getDoan_gioi_thieu() { return doan_gioi_thieu; }
    public void setDoan_gioi_thieu(String doan_gioi_thieu) { this.doan_gioi_thieu = doan_gioi_thieu; }

    public String getTieu_de_hang_muc() { return tieu_de_hang_muc; }
    public void setTieu_de_hang_muc(String tieu_de_hang_muc) { this.tieu_de_hang_muc = tieu_de_hang_muc; }

    public String getLoi_ket() { return loi_ket; }
    public void setLoi_ket(String loi_ket) { this.loi_ket = loi_ket; }

    public List<HangMuc> getDanh_sach_hang_muc() { return danh_sach_hang_muc; }
    public void setDanh_sach_hang_muc(List<HangMuc> danh_sach_hang_muc) { this.danh_sach_hang_muc = danh_sach_hang_muc; }
}