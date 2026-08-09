package com.phongkham.booking.repository;

import com.phongkham.booking.entity.ThongBao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThongBaoRepository extends JpaRepository<ThongBao, Long> {

    // Lấy danh sách thông báo của người dùng, mới nhất trước
    List<ThongBao> findByNguoiDungIdOrderByNgayTaoDesc(Long nguoiDungId);

    // Đếm số thông báo chưa đọc của người dùng
    long countByNguoiDungIdAndDaDocFalse(Long nguoiDungId);

    // Kiểm tra đã có thông báo chưa đọc cùng loại cho một lịch khám (tránh tạo lặp thông báo TAI_KHAM)
    boolean existsByNguoiDungIdAndLichKhamIdAndLoai(Long nguoiDungId, Integer lichKhamId, String loai);
}

