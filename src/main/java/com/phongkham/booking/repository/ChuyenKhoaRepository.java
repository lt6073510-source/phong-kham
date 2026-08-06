package com.phongkham.booking.repository;

import com.phongkham.booking.entity.ChuyenKhoa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChuyenKhoaRepository extends JpaRepository<ChuyenKhoa, Integer> {

    // Đổi tên hàm thành findByMaKhoa (Spring JPA sẽ hiểu đúng thuộc tính ma_khoa)
    Optional<ChuyenKhoa> findByMaKhoa(String maKhoa);

}