package com.phongkham.booking.controller;

import com.phongkham.booking.entity.LichKham;
import com.phongkham.booking.service.LichKhamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin("*") // Thêm để tránh lỗi CORS khi Frontend gọi API
@RestController
@RequestMapping("/api/lichkham")
public class LichKhamController {

    private final LichKhamService lichKhamService;

    public LichKhamController(LichKhamService lichKhamService) {
        this.lichKhamService = lichKhamService;
    }

    // 1. Lấy danh sách lịch khám (Có lọc theo trangThai)
    @GetMapping
    public ResponseEntity<List<LichKham>> getAllLichKham(@RequestParam(value = "status", required = false) String status) {
        return ResponseEntity.ok(lichKhamService.getAllLichKham(status));
    }

    // 2. Lấy chi tiết lịch khám theo ID
    @GetMapping("/{id}")
    public ResponseEntity<LichKham> getLichKhamById(@PathVariable("id") Integer id) {
        return lichKhamService.getLichKhamById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Lấy lịch khám theo Email
    @GetMapping("/email")
    public ResponseEntity<List<LichKham>> getLichByEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(lichKhamService.getLichByEmail(email));
    }

    // 4. Lấy lịch khám theo Số điện thoại
    @GetMapping("/sdt")
    public ResponseEntity<List<LichKham>> getLichBySoDienThoai(@RequestParam("soDienThoai") String soDienThoai) {
        return ResponseEntity.ok(lichKhamService.getLichBySoDienThoai(soDienThoai));
    }

    // 5. Lấy lịch khám theo Bác sĩ ID
    @GetMapping("/bacsi/{bacSiId}")
    public ResponseEntity<List<LichKham>> getLichByBacSi(@PathVariable("bacSiId") Integer bacSiId) {
        return ResponseEntity.ok(lichKhamService.getLichByBacSi(bacSiId));
    }

// 6. Lấy lịch khám theo Bệnh nhân ID
    @GetMapping("/benhnhan/{benhNhanId}")
    public ResponseEntity<List<LichKham>> getLichByBenhNhanId(@PathVariable("benhNhanId") Long benhNhanId) {
        return ResponseEntity.ok(lichKhamService.getLichByBenhNhanId(benhNhanId));
    }

    // 7a. Thống kê chung cho Admin
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getThongKeChung() {
        return ResponseEntity.ok(lichKhamService.getThongKeTrangThai());
    }

    // 7b. Bổ sung: Thống kê số lượng cho Dashboard Bác sĩ cụ thể
    @GetMapping("/stats/bacsi/{bacSiId}")
    public ResponseEntity<Map<String, Long>> getThongKeByBacSi(@PathVariable("bacSiId") Integer bacSiId) {
        return ResponseEntity.ok(lichKhamService.getThongKeTrangThaiByBacSi(bacSiId));
    }

    // 8. Bệnh nhân đặt lịch mới
    @PostMapping
    public ResponseEntity<LichKham> taoLichKham(@RequestBody LichKham lichKham) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lichKhamService.taoLichKham(lichKham));
    }

    // 9. Xác nhận lịch khám
    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> xacNhanLichKham(@PathVariable("id") Integer id) {
        try {
            LichKham updated = lichKhamService.xacNhanLichKham(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Xác nhận lịch khám thành công!", "data", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 10. Hủy lịch khám
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> huyLichKham(@PathVariable("id") Integer id, @RequestBody(required = false) Map<String, String> payload) {
        try {
            String reason = payload != null ? payload.getOrDefault("reason", payload.getOrDefault("lyDoHuy", "Lý do khác")) : "Lý do khác";
            LichKham updated = lichKhamService.huyLichKham(id, reason);
            return ResponseEntity.ok(Map.of("success", true, "message", "Hủy lịch khám thành công!", "data", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 11. Hoàn thành khám
    @PostMapping("/{id}/complete")
    public ResponseEntity<?> hoanThanhKham(@PathVariable("id") Integer id, @RequestBody(required = false) Map<String, String> payload) {
        try {
            String chanDoan = payload != null ? payload.getOrDefault("chanDoan", "") : "";
            String donThuoc = payload != null ? payload.getOrDefault("donThuoc", "") : "";
            String ngayTaiKham = payload != null ? payload.getOrDefault("ngayTaiKham", "") : "";

            LichKham updated = lichKhamService.hoanThanhKham(id, chanDoan, donThuoc, ngayTaiKham);
            return ResponseEntity.ok(Map.of("success", true, "message", "Lưu hồ sơ khám thành công!", "data", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

// 12. Xóa lịch khám theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaLichKham(@PathVariable("id") Integer id) {
        lichKhamService.xoaLichKham(id);
        return ResponseEntity.noContent().build();
    }
}
