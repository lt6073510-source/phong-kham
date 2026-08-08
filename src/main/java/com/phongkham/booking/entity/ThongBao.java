package com.phongkham.booking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "thongbao")
public class ThongBao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID người dùng nhận thông báo (bệnh nhân)
    @Column(name = "nguoi_dung_id")
    private Long nguoiDungId;

    @Column(name = "noi_dung", columnDefinition = "TEXT")
    private String noiDung;

    // Loại thông báo: XAC_NHAN / HUY / HOAN_THANH / TAI_KHAM
    @Column(name = "loai", length = 50)
    private String loai;

    @Column(name = "da_doc")
    private Boolean daDoc = false;

    @Column(name = "lich_kham_id")
    private Integer lichKhamId;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    public ThongBao() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNguoiDungId() { return nguoiDungId; }
    public void setNguoiDungId(Long nguoiDungId) { this.nguoiDungId = nguoiDungId; }

    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }

    public String getLoai() { return loai; }
    public void setLoai(String loai) { this.loai = loai; }

    public Boolean getDaDoc() { return daDoc; }
    public void setDaDoc(Boolean daDoc) { this.daDoc = daDoc; }

    public Integer getLichKhamId() { return lichKhamId; }
    public void setLichKhamId(Integer lichKhamId) { this.lichKhamId = lichKhamId; }

    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
}

