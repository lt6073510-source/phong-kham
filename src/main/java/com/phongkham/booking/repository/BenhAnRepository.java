package com.phongkham.booking.repository;

import com.phongkham.booking.entity.BenhAn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BenhAnRepository extends JpaRepository<BenhAn, Long> {

    // 1. Tìm hồ sơ bệnh án theo ID của lịch khám (lichKhamId là Integer như đã sửa ở BenhAn.java)
    Optional<BenhAn> findByLichKhamId(Integer lichKhamId);

    // 2. Kiểm tra xem lịch khám này đã được lập bệnh án chưa
    boolean existsByLichKhamId(Integer lichKhamId);
}