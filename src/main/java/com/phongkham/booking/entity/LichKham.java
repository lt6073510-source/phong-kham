package com.phongkham.booking.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "lichkham")
public class LichKham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Đổi kiểu từ Integer -> Long để trùng khớp với id bên NguoiDungBenhNhan
    @Column(name = "benh_nhan_id", insertable = false, updatable = false)
    private Long benhNhanId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "benh_nhan_id")
    private NguoiDungBenhNhan benhNhan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bac_si_id")
    private BacSi bacSi;

    private Integer lichLamViecId;

    private String ngayKham;

    private String gioKham;

    private String trangThai; // CHO_XAC_NHAN, DA_XAC_NHAN, DA_KHAM, DA_HUY

    @Column(columnDefinition = "TEXT")
    private String ghiChu;

    private LocalDateTime ngayDat;

    private String chanDoan;

    private String donThuoc;

    @Column(columnDefinition = "TEXT")
    private String ghiChuBacSi;

    private LocalTime gioBatDau;

    private LocalTime gioKetThuc;

    private String lyDoHuy;

    private String tenBacSi;

    private String tenBenhNhan;

    @Column(columnDefinition = "TEXT")
    private String tienSuBenh;

    private String trieuChung;

    private Long chuyenKhoaId;

    private String email;

    private String hoTenBenhNhan;

    private String lyDoDoi;

    private String ngayTaiKham;

    private LocalDateTime ngayTao;

    private Long nguoiDungId;

    private String soDienThoai;

    public LichKham() {
    }

    // --- Helper methods ---

    public NguoiDungBenhNhan getBenhNhan() {
        return benhNhan;
    }

    public void setBenhNhan(NguoiDungBenhNhan benhNhan) {
        this.benhNhan = benhNhan;
        if (benhNhan != null) {
            this.benhNhanId = benhNhan.getId();
        }
    }

    public Long getBacSiId() {
        if (bacSi == null || bacSi.getId() == null) {
            return null;
        }
        return Long.valueOf(bacSi.getId().toString());
    }

    public void setBacSiId(Long bacSiId) {
        if (bacSiId != null) {
            if (this.bacSi == null) {
                this.bacSi = new BacSi();
            }
            this.bacSi.setId(bacSiId.intValue());
        }
    }

    // --- Getters and Setters gốc ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getBenhNhanId() {
        return benhNhanId;
    }

    public void setBenhNhanId(Long benhNhanId) {
        this.benhNhanId = benhNhanId;
    }

    public BacSi getBacSi() {
        return bacSi;
    }

    public void setBacSi(BacSi bacSi) {
        this.bacSi = bacSi;
    }

    public Integer getLichLamViecId() {
        return lichLamViecId;
    }

    public void setLichLamViecId(Integer lichLamViecId) {
        this.lichLamViecId = lichLamViecId;
    }

    public String getNgayKham() {
        return ngayKham;
    }

    public void setNgayKham(String ngayKham) {
        this.ngayKham = ngayKham;
    }

    public String getGioKham() {
        return gioKham;
    }

    public void setGioKham(String gioKham) {
        this.gioKham = gioKham;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public LocalDateTime getNgayDat() {
        return ngayDat;
    }

    public void setNgayDat(LocalDateTime ngayDat) {
        this.ngayDat = ngayDat;
    }

    public String getChanDoan() {
        return chanDoan;
    }

    public void setChanDoan(String chanDoan) {
        this.chanDoan = chanDoan;
    }

    public String getDonThuoc() {
        return donThuoc;
    }

    public void setDonThuoc(String donThuoc) {
        this.donThuoc = donThuoc;
    }

    public String getGhiChuBacSi() {
        return ghiChuBacSi;
    }

    public void setGhiChuBacSi(String ghiChuBacSi) {
        this.ghiChuBacSi = ghiChuBacSi;
    }

    public LocalTime getGioBatDau() {
        return gioBatDau;
    }

    public void setGioBatDau(LocalTime gioBatDau) {
        this.gioBatDau = gioBatDau;
    }

    public LocalTime getGioKetThuc() {
        return gioKetThuc;
    }

    public void setGioKetThuc(LocalTime gioKetThuc) {
        this.gioKetThuc = gioKetThuc;
    }

    public String getLyDoHuy() {
        return lyDoHuy;
    }

    public void setLyDoHuy(String lyDoHuy) {
        this.lyDoHuy = lyDoHuy;
    }

    public String getTenBacSi() {
        return tenBacSi;
    }

    public void setTenBacSi(String tenBacSi) {
        this.tenBacSi = tenBacSi;
    }

    public String getTenBenhNhan() {
        return tenBenhNhan;
    }

    public void setTenBenhNhan(String tenBenhNhan) {
        this.tenBenhNhan = tenBenhNhan;
    }

    public String getTienSuBenh() {
        return tienSuBenh;
    }

    public void setTienSuBenh(String tienSuBenh) {
        this.tienSuBenh = tienSuBenh;
    }

    public String getTrieuChung() {
        return trieuChung;
    }

    public void setTrieuChung(String trieuChung) {
        this.trieuChung = trieuChung;
    }

    public Long getChuyenKhoaId() {
        return chuyenKhoaId;
    }

    public void setChuyenKhoaId(Long chuyenKhoaId) {
        this.chuyenKhoaId = chuyenKhoaId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHoTenBenhNhan() {
        return hoTenBenhNhan;
    }

    public void setHoTenBenhNhan(String hoTenBenhNhan) {
        this.hoTenBenhNhan = hoTenBenhNhan;
    }

    public String getLyDoDoi() {
        return lyDoDoi;
    }

    public void setLyDoDoi(String lyDoDoi) {
        this.lyDoDoi = lyDoDoi;
    }

    public String getNgayTaiKham() {
        return ngayTaiKham;
    }

    public void setNgayTaiKham(String ngayTaiKham) {
        this.ngayTaiKham = ngayTaiKham;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public Long getNguoiDungId() {
        return nguoiDungId;
    }

    public void setNguoiDungId(Long nguoiDungId) {
        this.nguoiDungId = nguoiDungId;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }
}