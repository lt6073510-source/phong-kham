package com.phongkham.booking.controller;

import com.phongkham.booking.entity.LichKham;
import com.phongkham.booking.service.LichKhamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

// API phu tro cho cac trang Bac si
// GET /api/ho-so-benh-an/{id}  : lay chi tiet ho so benh an (don thuoc)
// POST /api/nhac-nho/gui/{id}  : gui nhac nho tai kham
@RestController
@CrossOrigin("*")
public class BacSiPhongKhamApiController {

    private final LichKhamService lichKhamService;

    public BacSiPhongKhamApiController(LichKhamService lichKhamService) {
        this.lichKhamService = lichKhamService;
    }

    @GetMapping("/api/ho-so-benh-an/{id}")
    public ResponseEntity<?> getChiTietHoSo(@PathVariable("id") Integer id) {
        try {
            return lichKhamService.getLichKhamById(id)
                    .<ResponseEntity<?>>map(lk -> {
                        Map<String, Object> data = new HashMap<>();
                        data.put("id", lk.getId());
                        data.put("chanDoan", lk.getChanDoan() != null ? lk.getChanDoan() : "");
                        data.put("donThuoc", lk.getDonThuoc() != null ? lk.getDonThuoc() : "");
                        data.put("ngayTaiKham", lk.getNgayTaiKham() != null ? lk.getNgayTaiKham() : "");
                        data.put("ngayKham", lk.getNgayKham() != null ? lk.getNgayKham() : "");
                        data.put("hoTenBenhNhan", lk.getHoTenBenhNhan() != null ? lk.getHoTenBenhNhan() : "");
                        return ResponseEntity.ok((Object) data);
                    })
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("success", false, "message", "Khong tim thay ho so benh an!")));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Loi he thong: " + e.getMessage()));
        }
    }

    @PostMapping("/api/nhac-nho/gui/{id}")
    public ResponseEntity<?> guiNhacNho(@PathVariable("id") Integer id) {
        try {
            boolean exists = lichKhamService.getLichKhamById(id).isPresent();
            if (!exists) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Khong tim thay lich kham!"));
            }
            return ResponseEntity.ok(Map.of("success", true, "message", "Da gui thong bao nhac lich tai kham thanh cong!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Loi khi gui nhac nho: " + e.getMessage()));
        }
    }
}
