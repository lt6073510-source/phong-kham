package com.phongkham.booking.repository;

import com.phongkham.booking.entity.NguoiDungBenhNhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KhoNguoiDungBenhNhan extends JpaRepository<NguoiDungBenhNhan, Long> {

    // 1. Tìm người dùng theo Email (Phục vụ Đăng nhập & Xác thực)
    Optional<NguoiDungBenhNhan> findByEmail(String email);

    // 2. Kiểm tra Email đã tồn tại chưa (Phục vụ Đăng ký tài khoản)
    boolean existsByEmail(String email);

    // 3. Tìm danh sách người dùng theo vai trò (Dành cho Admin lọc Bệnh nhân / Admin)
    List<NguoiDungBenhNhan> findByVaiTro(String vaiTro);

    // 4. Đếm tổng số lượng Bệnh nhân đăng ký trong hệ thống (Dành cho Admin xem thống kê)
    long countByVaiTro(String vaiTro);

// 5. Tìm theo số điện thoại (Hỗ trợ tra cứu thông tin nhanh)
    Optional<NguoiDungBenhNhan> findBySoDienThoai(String soDienThoai);

    // 5a. Lấy 1 bệnh nhân đầu tiên theo SĐT (tránh lỗi NonUniqueResult khi có nhiều tài khoản trùng SĐT)
    Optional<NguoiDungBenhNhan> findFirstBySoDienThoai(String soDienThoai);
}