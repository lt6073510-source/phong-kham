package com.phongkham.booking.controller;

import com.phongkham.booking.entity.BacSi;
import com.phongkham.booking.repository.BacSiRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bac-si")
public class BacSiApiController {

    private final BacSiRepository bacSiRepository;

    public BacSiApiController(BacSiRepository bacSiRepository) {
        this.bacSiRepository = bacSiRepository;
    }

    @GetMapping("/by-khoa")
    public ResponseEntity<?> getBacSiByKhoa(@RequestParam("khoaId") Integer khoaId) {
        try {
            if (khoaId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Mã chuyên khoa không được để trống!"));
            }

            // Gọi hàm chỉ lấy Bác sĩ đang "HOAT_DONG" thuộc Chuyên khoa đó (dùng hàm số 5 trong Repository của bạn)
            List<BacSi> list = bacSiRepository.findByChuyenKhoa_IdAndTrangThai(khoaId, "HOAT_DONG");

            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Lỗi khi truy xuất danh sách bác sĩ!"));
        }
    }
}