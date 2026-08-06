package com.phongkham.booking.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "lichlamviec")
public class LichLamViec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Chuyển sang LAZY để tránh query thừa, thêm JsonIgnoreProperties để serialize JSON an toàn
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bac_si_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "danhSachLichKham", "danhSachLichLamViec"})
    private BacSi bacSi;

    @Column(name = "ngay_lam_viec", nullable = false)
    private LocalDate ngayLamViec;

    @Column(name = "gio_bat_dau", nullable = false)
    private LocalTime gioBatDau;

    @Column(name = "gio_ket_thuc", nullable = false)
    private LocalTime gioKetThuc;

    @Column(name = "trang_thai")
    private String trangThai = "CON_TRONG"; // Mặc định là còn trống

    public LichLamViec() {}

    public LichLamViec(BacSi bacSi, LocalDate ngayLamViec, LocalTime gioBatDau, LocalTime gioKetThuc, String trangThai) {
        this.bacSi = bacSi;
        this.ngayLamViec = ngayLamViec;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.trangThai = trangThai;
    }

    // --- Getter & Setter ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public BacSi getBacSi() { return bacSi; }
    public void setBacSi(BacSi bacSi) { this.bacSi = bacSi; }

    public LocalDate getNgayLamViec() { return ngayLamViec; }
    public void setNgayLamViec(LocalDate ngayLamViec) { this.ngayLamViec = ngayLamViec; }

    public LocalTime getGioBatDau() { return gioBatDau; }
    public void setGioBatDau(LocalTime gioBatDau) { this.gioBatDau = gioBatDau; }

    public LocalTime getGioKetThuc() { return gioKetThuc; }
    public void setGioKetThuc(LocalTime gioKetThuc) { this.gioKetThuc = gioKetThuc; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}