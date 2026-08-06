package com.phongkham.booking.controller;

import com.phongkham.booking.entity.BacSi;
import com.phongkham.booking.entity.LichKham;
import com.phongkham.booking.repository.BacSiRepository;
import com.phongkham.booking.service.LichKhamService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentApiController {

    private final LichKhamService lichKhamService;
    private final BacSiRepository bacSiRepository;

    public AppointmentApiController(LichKhamService lichKhamService, BacSiRepository bacSiRepository) {
        this.lichKhamService = lichKhamService;
        this.bacSiRepository = bacSiRepository;
    }

    /**
     * Đọc Doctor ID linh hoạt và an toàn từ Session
     */
    private Integer getDoctorIdFromSession(HttpSession session) {
        if (session == null) return null;

        // 1. Kiểm tra vai trò (Role) trong Session, nếu là BENH_NHAN thì không cho phép truy cập API bác sĩ
        Object roleObj = session.getAttribute("userRole");
        if (roleObj != null && "BENH_NHAN".equalsIgnoreCase(roleObj.toString())) {
            return null;
        }

        // 2. Ưu tiên lấy trực tiếp doctorId / bacSiId được lưu lúc đăng nhập
        Object idObj = session.getAttribute("doctorId");
        if (idObj == null) idObj = session.getAttribute("bacSiId");
        if (idObj == null) idObj = session.getAttribute("bacsiId");

        // 3. Nếu lưu nguyên object Bác sĩ trong Session
        if (idObj == null) {
            Object userObj = session.getAttribute("userLuuSinh");
            if (userObj == null) userObj = session.getAttribute("bacSi");
            if (userObj == null) userObj = session.getAttribute("user");

            if (userObj instanceof BacSi) {
                return ((BacSi) userObj).getId();
            }
        }

        // 4. Ép kiểu dữ liệu an toàn
        if (idObj == null) return null;
        if (idObj instanceof Integer) return (Integer) idObj;
        if (idObj instanceof Long) return ((Long) idObj).intValue();
        if (idObj instanceof String) {
            try {
                return Integer.parseInt((String) idObj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    // 1. API LẤY THÔNG TIN BÁC SĨ ĐANG ĐĂNG NHẬP
    @GetMapping("/bacsi/me")
    public ResponseEntity<?> getProfileMe(HttpSession session) {
        Integer doctorId = getDoctorIdFromSession(session);
        if (doctorId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "Chưa đăng nhập bác sĩ!"));
        }

        return bacSiRepository.findById(doctorId)
                .map(bs -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", bs.getId());
                    response.put("hoTen", bs.getHoTen());
                    response.put("hocVi", bs.getHocVi());
                    response.put("moTa", bs.getMoTa());
                    response.put("email", bs.getEmail());
                    response.put("soDienThoai", bs.getSoDienThoai());
                    return ResponseEntity.ok((Object) response);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy bác sĩ!"));
    }

    // 2. API CẬP NHẬT THÔNG TIN BÁC SĨ
    @PutMapping("/bacsi/update")
    public ResponseEntity<?> updateProfile(@RequestBody BacSi bacSiDto, HttpSession session) {
        Integer doctorId = getDoctorIdFromSession(session);
        if (doctorId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chưa đăng nhập bác sĩ!");
        }

        return bacSiRepository.findById(doctorId)
                .map(bs -> {
                    if (bacSiDto.getHoTen() != null) bs.setHoTen(bacSiDto.getHoTen());
                    if (bacSiDto.getHocVi() != null) bs.setHocVi(bacSiDto.getHocVi());
                    if (bacSiDto.getMoTa() != null) bs.setMoTa(bacSiDto.getMoTa());
                    if (bacSiDto.getSoDienThoai() != null) bs.setSoDienThoai(bacSiDto.getSoDienThoai());
                    
                    bacSiRepository.save(bs);
                    return ResponseEntity.ok((Object) Map.of("success", true, "message", "Cập nhật thành công!"));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy bác sĩ!"));
    }

    // 3. API LẤY DANH SÁCH LỊCH KHÁM CỦA BÁC SĨ
    @GetMapping
    public ResponseEntity<?> getAppointmentsByStatus(
            @RequestParam(value = "status", required = false) String status, 
            HttpSession session) {
        try {
            Integer doctorId = getDoctorIdFromSession(session);
            if (doctorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Chưa xác định được ID bác sĩ. Vui lòng đăng nhập lại!"));
            }

            List<LichKham> list = lichKhamService.getLichByBacSi(doctorId);
            if (list == null) {
                return ResponseEntity.ok(new ArrayList<>());
            }

            if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status.trim())) {
                String statusFilter = status.trim().toUpperCase();
                list = list.stream()
                        .filter(l -> l.getTrangThai() != null && l.getTrangThai().trim().equalsIgnoreCase(statusFilter))
                        .collect(Collectors.toList());
            }

            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    // 4. API LẤY CHI TIẾT LỊCH KHÁM
    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointmentDetail(@PathVariable Integer id) {
        try {
            return lichKhamService.getLichKhamById(id)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lịch khám!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống!");
        }
    }

    // 5. API XÁC NHẬN LỊCH KHÁM
    @RequestMapping(value = "/{id}/confirm", method = {RequestMethod.PUT, RequestMethod.POST})
    public ResponseEntity<?> confirmAppointment(@PathVariable Integer id) {
        try {
            LichKham updated = lichKhamService.xacNhanLichKham(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Đã xác nhận lịch hẹn", "data", updated));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 6. API HỦY LỊCH KHÁM
    @RequestMapping(value = "/{id}/cancel", method = {RequestMethod.PUT, RequestMethod.POST})
    public ResponseEntity<?> cancelAppointment(
            @PathVariable Integer id, 
            @RequestParam(value = "lyDoHuy", required = false) String lyDoQueryParam,
            @RequestBody(required = false) Map<String, Object> body) {
        try {
            String reason = "Bác sĩ hủy lịch";

            if (lyDoQueryParam != null && !lyDoQueryParam.isBlank()) {
                reason = lyDoQueryParam;
            } else if (body != null) {
                if (body.get("reason") != null) {
                    reason = String.valueOf(body.get("reason"));
                } else if (body.get("lyDoHuy") != null) {
                    reason = String.valueOf(body.get("lyDoHuy"));
                }
            }

            LichKham updated = lichKhamService.huyLichKham(id, reason);
            return ResponseEntity.ok(Map.of("success", true, "message", "Đã hủy lịch hẹn thành công", "data", updated));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 7. API HOÀN THÀNH KHÁM & KÊ ĐƠN
    @RequestMapping(value = "/{id}/complete", method = {RequestMethod.PUT, RequestMethod.POST})
    public ResponseEntity<?> completeAppointment(@PathVariable Integer id, @RequestBody(required = false) Map<String, Object> body) {
        try {
            String chanDoan = "";
            String donThuoc = "";
            String ngayTaiKham = "";

            if (body != null) {
                Object cdObj = body.getOrDefault("chanDoan", body.get("chandoan"));
                Object dtObj = body.getOrDefault("donThuoc", body.get("donthuoc"));
                Object ntkObj = body.getOrDefault("ngayTaiKham", body.get("ngaytaikham"));

                if (cdObj != null) chanDoan = cdObj.toString();
                if (dtObj != null) donThuoc = dtObj.toString();
                if (ntkObj != null) ngayTaiKham = ntkObj.toString();
            }

            LichKham updated = lichKhamService.hoanThanhKham(id, chanDoan, donThuoc, ngayTaiKham);
            return ResponseEntity.ok(Map.of("success", true, "message", "Đã hoàn thành lượt khám", "data", updated));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}