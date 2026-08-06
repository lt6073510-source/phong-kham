package com.phongkham.booking.repository;

import com.phongkham.booking.entity.NguoiDungBenhNhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NguoiDungBenhNhanRepository extends JpaRepository<NguoiDungBenhNhan, Long> {

    // Tìm người dùng theo Email (dùng cho Đăng nhập / Kiểm tra trùng Email)
    Optional<NguoiDungBenhNhan> findByEmail(String email);

    // Kiểm tra Email đã tồn tại trong hệ thống chưa
    boolean existsByEmail(String email);
}