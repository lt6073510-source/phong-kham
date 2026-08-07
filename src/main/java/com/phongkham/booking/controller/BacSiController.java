package com.phongkham.booking.controller;

import com.phongkham.booking.entity.BacSi;
import com.phongkham.booking.entity.LichKham;
import com.phongkham.booking.service.BacSiService;
import com.phongkham.booking.service.LichKhamService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class BacSiController {

    private final BacSiService bacSiService;
    private final LichKhamService lichKhamService;

    public BacSiController(BacSiService bacSiService, LichKhamService lichKhamService) {
        this.bacSiService = bacSiService;
        this.lichKhamService = lichKhamService;
    }

    // Hàm tiện ích lấy email Bác sĩ từ Session
    private String getDoctorEmailFromSession(HttpSession session) {
        if (session == null) return null;
        String email = (String) session.getAttribute("doctorUser");
        if (email == null || email.isBlank()) {
            email = (String) session.getAttribute("userEmail");
        }
        return email;
    }

    // 1. Dashboard chính của bác sĩ
    @GetMapping({"/bac_si", "/bacsi/dashboard", "/bac-si/dashboard"})
    public String trangDashboardBacSi(HttpSession session, Model model) {
        String doctorEmail = getDoctorEmailFromSession(session);
        
        if (doctorEmail == null) {
            return "redirect:/dang_nhap";
        }

        Optional<BacSi> bsOpt = bacSiService.getBacSiByEmail(doctorEmail);
        if (bsOpt.isPresent()) {
            BacSi bs = bsOpt.get();
            
            // Đồng bộ Session ID cho Bác sĩ
            session.setAttribute("doctorId", bs.getId());
            session.setAttribute("bacSiId", bs.getId());
            session.setAttribute("doctorUser", bs.getEmail());

            model.addAttribute("doctorId", bs.getId());
            model.addAttribute("tenBacSi", bs.getHoTen());
            model.addAttribute("departmentName", bs.getChuyenKhoa() != null ? bs.getChuyenKhoa().getTenChuyenKhoa() : "Chuyên khoa: Chưa cập nhật");

            // Xử lý lấy lịch khám an toàn bằng try-catch
            List<LichKham> danhSachLich = new ArrayList<>();
            try {
                if (bs.getId() != null) {
                    List<LichKham> list = lichKhamService.getLichByBacSi(bs.getId().intValue());
                    if (list != null) {
                        danhSachLich = list;
                    }
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi truy vấn lịch khám bác sĩ: " + e.getMessage());
            }

            model.addAttribute("appointments", danhSachLich);
            model.addAttribute("dsLichKham", danhSachLich);

            // Thống kê trạng thái lịch khám
            final List<LichKham> finalLich = danhSachLich;
            model.addAttribute("countPending", finalLich.stream()
                    .filter(l -> l.getTrangThai() == null || "CHO_XAC_NHAN".equalsIgnoreCase(l.getTrangThai()) || "PENDING".equalsIgnoreCase(l.getTrangThai()))
                    .count());
            
            model.addAttribute("countConfirmed", finalLich.stream()
                    .filter(l -> "DA_XAC_NHAN".equalsIgnoreCase(l.getTrangThai()) || "CONFIRMED".equalsIgnoreCase(l.getTrangThai()))
                    .count());
            
            model.addAttribute("countCompleted", finalLich.stream()
                    .filter(l -> "HOAN_THANH".equalsIgnoreCase(l.getTrangThai()) || "COMPLETED".equalsIgnoreCase(l.getTrangThai()))
                    .count());
            
            model.addAttribute("countCancelled", finalLich.stream()
                    .filter(l -> "DA_HUY".equalsIgnoreCase(l.getTrangThai()) || "CANCELLED".equalsIgnoreCase(l.getTrangThai()))
                    .count());
        } else {
            session.removeAttribute("doctorUser");
            return "redirect:/dang_nhap";
        }

        return "bac_si";
    }

    // 2. Quản lý Hồ sơ bệnh án
    @GetMapping({"/bac_si/ho_so_benh_an", "/bac_si/ho-so-benh-an", "/bac_si/chi_tiet_ho_so", "/bac_si/chi-tiet-ho-so"})
    public String trangHoSoBenhAn(@RequestParam(value = "patientId", required = false) Long patientId,
                                  @RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "date", required = false) String date,
                                  HttpSession session, 
                                  Model model) {
        String doctorEmail = getDoctorEmailFromSession(session);
        if (doctorEmail == null) {
            return "redirect:/dang_nhap";
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedDate", date);
        model.addAttribute("selectedPatientId", patientId);

        return "chi_tiet_ho_so"; 
    }

    // 3. Quản lý Nhắc nhở
    @GetMapping({"/bac_si/nhac_nho", "/bac_si/nhac-nho"})
    public String trangNhacNho(HttpSession session, Model model) {
        String doctorEmail = getDoctorEmailFromSession(session);
        if (doctorEmail == null) {
            return "redirect:/dang_nhap";
        }

        return "nhac_nho"; 
    }
}