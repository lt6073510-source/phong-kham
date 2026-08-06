package com.phongkham.booking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class HangMuc {

    @Column(name = "biet_tuyen")
    private String biet_tuyen; // Chứa biểu tượng/icon (ví dụ: "❤️", "🫁", "🦴")

    @Column(name = "ten_hang_muc")
    private String ten_hang_muc; // Tên hạng mục dịch vụ (ví dụ: "Hệ Tim mạch")

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String mo_ta; // Mô tả ngắn dịch vụ

    // --- Constructor không đối số (Bắt buộc phải có đối với JPA) ---
    public HangMuc() {}

    // --- Constructor đầy đủ 3 tham số (Dùng cho DataInitializer) ---
    public HangMuc(String biet_tuyen, String ten_hang_muc, String mo_ta) {
        this.biet_tuyen = biet_tuyen;
        this.ten_hang_muc = ten_hang_muc;
        this.mo_ta = mo_ta;
    }

    // --- Getters & Setters ---
    public String getBiet_tuyen() {
        return biet_tuyen;
    }

    public void setBiet_tuyen(String biet_tuyen) {
        this.biet_tuyen = biet_tuyen;
    }

    public String getTen_hang_muc() {
        return ten_hang_muc;
    }

    public void setTen_hang_muc(String ten_hang_muc) {
        this.ten_hang_muc = ten_hang_muc;
    }

    public String getMo_ta() {
        return mo_ta;
    }

    public void setMo_ta(String mo_ta) {
        this.mo_ta = mo_ta;
    }
}