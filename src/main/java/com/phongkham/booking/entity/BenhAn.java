package com.phongkham.booking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "benhan")
public class BenhAn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Đồng bộ kiểu dữ liệu Integer giống với LichKham.id
    private Integer lichKhamId;

    @Column(columnDefinition = "TEXT")
    private String chanDoan;

    @Column(columnDefinition = "TEXT")
    private String donThuoc;

    @Column(columnDefinition = "TEXT")
    private String ghiChu;

    private LocalDateTime ngayKham = LocalDateTime.now();

    public BenhAn() {}

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getLichKhamId() { return lichKhamId; }
    public void setLichKhamId(Integer lichKhamId) { this.lichKhamId = lichKhamId; }

    public String getChanDoan() { return chanDoan; }
    public void setChanDoan(String chanDoan) { this.chanDoan = chanDoan; }

    public String getDonThuoc() { return donThuoc; }
    public void setDonThuoc(String donThuoc) { this.donThuoc = donThuoc; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public LocalDateTime getNgayKham() { return ngayKham; }
    public void setNgayKham(LocalDateTime ngayKham) { this.ngayKham = ngayKham; }
}