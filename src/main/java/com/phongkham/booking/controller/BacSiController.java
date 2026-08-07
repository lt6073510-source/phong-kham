package com.phongkham.booking.controller;

import com.phongkham.booking.entity.BacSi;
import com.phongkham.booking.service.BacSiService;
import com.phongkham.booking.service.LichKhamService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class BacSiController {

    private final BacSiService bacSiService;
    private final LichKhamService lichKhamService;

    public BacSiController(BacSiService bacSiService, LichKhamService lichKhamService) {
        this.bacSiService = bacSiService;
        this.lichKhamService = lichKhamService;
    }

    // 1. Dashboard chính của bác sĩ
    @GetMapping({"/bac_si", "/bacsi/dashboard", "/bac-si/dashboard"})
    public String trangDashboardBacSi(HttpSession session, Model model) {
        // SỬA: Đổi "doctorUser" -> "userEmail" cho khớp với WebController
        String doctorEmail = (String) session.getAttribute("userEmail");
        if (doctorEmail == null) {
            return "redirect:/dang-nhap"; // SỬA: Đổi sang trang đăng nhập chung
        }

        Optional<BacSi> bsOpt = bacSiService.getBacSiByEmail(doctorEmail);
        if (bsOpt.isPresent()) {
            BacSi bs = bsOpt.get();
            session.setAttribute("doctorId", bs.getId());
            model.addAttribute("doctorId", bs.getId());
            model.addAttribute("tenBacSi", bs.getHoTen());
            model.addAttribute("departmentName", bs.getChuyenKhoa() != null ? bs.getChuyenKhoa().getTenChuyenKhoa() : "Chuyên khoa: Chưa cập nhật");

            java.util.List<com.phongkham.booking.entity.LichKham> danhSachLich = lichKhamService.getLichByBacSi(bs.getId());
            model.addAttribute("appointments", danhSachLich);
            model.addAttribute("dsLichKham", danhSachLich);
            model.addAttribute("countPending", danhSachLich.stream().filter(l -> l.getTrangThai() == null || "CHO_XAC_NHAN".equalsIgnoreCase(l.getTrangThai()) || "PENDING".equalsIgnoreCase(l.getTrangThai())).count());
            model.addAttribute("countConfirmed", danhSachLich.stream().filter(l -> "DA_XAC_NHAN".equalsIgnoreCase(l.getTrangThai()) || "CONFIRMED".equalsIgnoreCase(l.getTrangThai())).count());
            model.addAttribute("countCompleted", danhSachLich.stream().filter(l -> "HOAN_THANH".equalsIgnoreCase(l.getTrangThai()) || "COMPLETED".equalsIgnoreCase(l.getTrangThai())).count());
            model.addAttribute("countCancelled", danhSachLich.stream().filter(l -> "DA_HUY".equalsIgnoreCase(l.getTrangThai()) || "CANCELLED".equalsIgnoreCase(l.getTrangThai())).count());
        } else {
            session.removeAttribute("userEmail"); // SỬA: Key session
            return "redirect:/dang-nhap"; // SỬA: Chuyển hướng
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
        // SỬA: Đổi "doctorUser" -> "userEmail"
        String doctorEmail = (String) session.getAttribute("userEmail");
        if (doctorEmail == null) {
            return "redirect:/dang-nhap"; // SỬA
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedDate", date);
        model.addAttribute("selectedPatientId", patientId);

        // ĐÃ SỬA: Bỏ tiền tố "bac_si/" để Thymeleaf tìm đúng file templates/chi_tiet_ho_so.html
        return "chi_tiet_ho_so"; 
    }

    // 3. Quản lý Nhắc nhở
    @GetMapping({"/bac_si/nhac_nho", "/bac_si/nhac-nho"})
    public String trangNhacNho(HttpSession session, Model model) {
        // SỬA: Đổi "doctorUser" -> "userEmail"
        String doctorEmail = (String) session.getAttribute("userEmail");
        if (doctorEmail == null) {
            return "redirect:/dang-nhap"; // SỬA
        }

        // ĐÃ SỬA: Bỏ tiền tố "bac_si/" để Thymeleaf tìm đúng file templates/nhac_nho.html
        return "nhac_nho"; 
    }
}