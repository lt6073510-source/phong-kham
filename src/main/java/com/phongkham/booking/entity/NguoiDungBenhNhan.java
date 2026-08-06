package com.phongkham.booking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "nguoidungbenhnhan")
public class NguoiDungBenhNhan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ho_ten", nullable = false)
    private String hoTen;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "mat_khau", nullable = false)
    private String matKhau;

    // Phân vai trò người dùng (Ví dụ: "ROLE_USER", "ROLE_ADMIN", "ROLE_BAC_SI")
    @Column(name = "vai_tro", nullable = false)
    private String vaiTro = "ROLE_USER"; 

    // Trạng thái tài khoản (Ví dụ: "HOAT_DONG", "KHOA")
    @Column(name = "trang_thai")
    private String trangThai = "HOAT_DONG";

    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngayTao;

    // Tự động gán thời gian tạo khi thêm mới record
    @PrePersist
    protected void onCreate() {
        if (this.ngayTao == null) {
            this.ngayTao = LocalDateTime.now();
        }
        if (this.vaiTro == null) {
            this.vaiTro = "ROLE_USER";
        }
        if (this.trangThai == null) {
            this.trangThai = "HOAT_DONG";
        }
    }

    // ===== Constructors =====
    public NguoiDungBenhNhan() {
    }

    public NguoiDungBenhNhan(String hoTen, String email, String matKhau, String soDienThoai) {
        this.hoTen = hoTen;
        this.email = email;
        this.matKhau = matKhau;
        this.soDienThoai = soDienThoai;
        this.vaiTro = "ROLE_USER";
        this.trangThai = "HOAT_DONG";
    }

    // ===== Getters and Setters =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }
}