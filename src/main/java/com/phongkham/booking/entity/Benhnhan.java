package com.phongkham.booking.entity;



import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "benhnhan")
public class Benhnhan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // Sửa từ "stt" thành "id" để khớp với database
    private Long id;

    @Column(name = "ho_ten_benh_nhan")
    private String hoTen;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Column(name = "email")
    private String email;

    @Column(name = "mat_khau")
    private String matKhau;

    @Column(name = "ngay_kham")
    private String ngayKham;

    @Column(name = "gio_kham")
    private String gioKham;

    @Column(name = "chuyen_khoa_id")
    private String chuyenKhoaId;

    @Column(name = "bac_si_id")
    private String bacSiId;

    @Column(name = "trieu_chung", columnDefinition = "TEXT")
    private String trieuChung;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    // --- CÁC TRƯỜNG BỔ SUNG CHO TÍNH NĂNG HỦY / ĐỔI LỊCH ---
    @Column(name = "status")
    private String status = "CHO_XAC_NHAN"; // Trạng thái mặc định khi đặt lịch

    @Column(name = "ly_do_huy", columnDefinition = "TEXT")
    private String lyDoHuy;

    @Column(name = "ly_do_doi", columnDefinition = "TEXT")
    private String lyDoDoi;

    // --- GETTERS VÀ SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public String getNgayKham() { return ngayKham; }
    public void setNgayKham(String ngayKham) { this.ngayKham = ngayKham; }

    public String getGioKham() { return gioKham; }
    public void setGioKham(String gioKham) { this.gioKham = gioKham; }

    public String getChuyenKhoaId() { return chuyenKhoaId; }
    public void setChuyenKhoaId(String chuyenKhoaId) { this.chuyenKhoaId = chuyenKhoaId; }

    public String getBacSiId() { return bacSiId; }
    public void setBacSiId(String bacSiId) { this.bacSiId = bacSiId; }

    public String getTrieuChung() { return trieuChung; }
    public void setTrieuChung(String trieuChung) { this.trieuChung = trieuChung; }

    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLyDoHuy() { return lyDoHuy; }
    public void setLyDoHuy(String lyDoHuy) { this.lyDoHuy = lyDoHuy; }

    public String getLyDoDoi() { return lyDoDoi; }
    public void setLyDoDoi(String lyDoDoi) { this.lyDoDoi = lyDoDoi; }
}