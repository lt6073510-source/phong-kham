package com.phongkham.booking.service;

import com.phongkham.booking.entity.BacSi;
import com.phongkham.booking.repository.BacSiRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BacSiService {

    private final BacSiRepository bacSiRepository;

    public BacSiService(BacSiRepository bacSiRepository) {
        this.bacSiRepository = bacSiRepository;
    }

    // 1. Lấy tất cả bác sĩ
    public List<BacSi> getAllBacSi() {
        return bacSiRepository.findAll();
    }

    // 2. Lấy bác sĩ theo ID
    public Optional<BacSi> getBacSiById(Integer id) {
        return bacSiRepository.findById(id);
    }

    // 3. Lấy danh sách bác sĩ theo Chuyên khoa (Đã sửa thành findByChuyenKhoa_Id)
    public List<BacSi> getBacSiByChuyenKhoa(Integer chuyenKhoaId) {
        return bacSiRepository.findByChuyenKhoa_Id(chuyenKhoaId);
    }

    // 4. Lưu / Cập nhật bác sĩ
    public BacSi luuBacSi(BacSi bacSi) {
        return bacSiRepository.save(bacSi);
    }

    // 5. Xóa bác sĩ
    public void xoaBacSi(Integer id) {
        bacSiRepository.deleteById(id);
    }

    // 6. Lấy bác sĩ theo Email đăng nhập
    public Optional<BacSi> getBacSiByEmail(String email) {
        return bacSiRepository.findByEmail(email);
    }

    // 7. Kiểm tra xem Email bác sĩ đã tồn tại chưa
    public boolean existsByEmail(String email) {
        return bacSiRepository.existsByEmail(email);
    }
}