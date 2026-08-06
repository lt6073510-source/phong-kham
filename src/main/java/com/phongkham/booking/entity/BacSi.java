package com.phongkham.booking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bacsi")
public class BacSi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // === Thông tin Tài khoản & Đăng nhập ===
    @Column(name = "ho_ten", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @JsonIgnore // Không trả về mật khẩu khi serialize JSON ra Client
    @Column(name = "mat_khau", nullable = false)
    private String matKhau;

    @Column(name = "so_dien_thoai", length = 15)
    private String soDienThoai;

    @Column(name = "vai_tro", length = 20)
    private String vaiTro = "BAC_SI";

    @Column(name = "trang_thai", length = 20)
    private String trangThai = "HOAT_DONG";

    // === Thông tin Hồ sơ Chuyên môn ===
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chuyen_khoa_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "danhSachBacSi"})
    private ChuyenKhoa chuyenKhoa;

    @Column(name = "hoc_vi", length = 100)
    private String hocVi;

    @Column(name = "so_nam_kinh_nghiem")
    private Integer soNamKinhNghiem = 0;

    @Column(name = "gia_kham")
    private BigDecimal giaKham = new BigDecimal("200000.00");

    @Column(name = "anh")
    private String anh;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "ngay_tao", insertable = false, updatable = false)
    private LocalDateTime ngayTao;

    public BacSi() {}

    // Constructor phục vụ cho khởi tạo nhanh
    public BacSi(String hoTen, String email, String matKhau, String soDienThoai, String hocVi, ChuyenKhoa chuyenKhoa, String moTa) {
        this.hoTen = hoTen;
        this.email = email;
        this.matKhau = matKhau;
        this.soDienThoai = soDienThoai;
        this.hocVi = hocVi;
        this.chuyenKhoa = chuyenKhoa;
        this.moTa = moTa;
    }

    // --- Getter & Setter ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getVaiTro() { return vaiTro; }
    public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public ChuyenKhoa getChuyenKhoa() { return chuyenKhoa; }
    public void setChuyenKhoa(ChuyenKhoa chuyenKhoa) { this.chuyenKhoa = chuyenKhoa; }

    public String getHocVi() { return hocVi; }
    public void setHocVi(String hocVi) { this.hocVi = hocVi; }

    public Integer getSoNamKinhNghiem() { return soNamKinhNghiem; }
    public void setSoNamKinhNghiem(Integer soNamKinhNghiem) { this.soNamKinhNghiem = soNamKinhNghiem; }

    public BigDecimal getGiaKham() { return giaKham; }
    public void setGiaKham(BigDecimal giaKham) { this.giaKham = giaKham; }

    public String getAnh() { return anh; }
    public void setAnh(String anh) { this.anh = anh; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
}