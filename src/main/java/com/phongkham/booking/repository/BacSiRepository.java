package com.phongkham.booking.repository;

import com.phongkham.booking.entity.BacSi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BacSiRepository extends JpaRepository<BacSi, Integer> {
    
    // 1. Tìm danh sách bác sĩ theo ID Chuyên khoa (dùng ChuyenKhoa_Id)
    List<BacSi> findByChuyenKhoa_Id(Integer chuyenKhoaId);

    // 2. Tra cứu bác sĩ trực tiếp qua email đăng nhập
    Optional<BacSi> findByEmail(String email);

    // 3. Kiểm tra xem email đã tồn tại hay chưa
    boolean existsByEmail(String email);

    // 4. Tìm bác sĩ theo trạng thái hoạt động
    List<BacSi> findByTrangThai(String trangThai);

    // 5. [Gợi ý bổ sung] Tìm bác sĩ theo chuyên khoa và trạng thái hoạt động
    List<BacSi> findByChuyenKhoa_IdAndTrangThai(Integer chuyenKhoaId, String trangThai);
}