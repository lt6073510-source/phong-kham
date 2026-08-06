package com.phongkham.booking.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.phongkham.booking.entity.BacSi;
import com.phongkham.booking.repository.BacSiRepository;

import java.util.Optional;

@Controller
public class AdminAuthController {

    private final BacSiRepository bacSiRepository;

    public AdminAuthController(BacSiRepository bacSiRepository) {
        this.bacSiRepository = bacSiRepository;
    }

    @Value("${admin1.username:admin1}")
    private String admin1User;
    @Value("${admin1.password:AdminPass123!}")
    private String admin1Pass;

    @Value("${admin2.username:admin2}")
    private String admin2User;
    @Value("${admin2.password:AdminPass456!}")
    private String admin2Pass;

    // ==========================================
    // ENDPOINT CHO NHÂN VIÊN (ADMIN & BÁC SĨ)
    // ==========================================

    @GetMapping({"/login", "/dang_nhap_nhan_vien"})
    public String trangDangNhapNhanVien(HttpSession session) {
        if (session.getAttribute("adminUser") != null) {
            return "redirect:/admin";
        }
        if (session.getAttribute("doctorUser") != null) {
            return "redirect:/bac_si";
        }
        return "dang_nhap_nhan_vien";
    }

    @PostMapping("/dang_nhap_nhan_vien")
    public String xuLyDangNhap(@RequestParam("role") String role,
                               @RequestParam("email") String email,
                               @RequestParam("password") String password,
                               HttpServletRequest request,
                               HttpSession session,
                               Model model) {

        // --- ADMIN ĐĂNG NHẬP ---
        if ("ADMIN".equalsIgnoreCase(role)) {
            boolean isAdmin1 = admin1User.equalsIgnoreCase(email) && admin1Pass.equals(password);
            boolean isAdmin2 = admin2User.equalsIgnoreCase(email) && admin2Pass.equals(password);

            if (isAdmin1 || isAdmin2) {
                if (session != null) {
                    session.invalidate();
                }
                HttpSession newSession = request.getSession(true);
                newSession.setAttribute("adminUser", email);
                newSession.setAttribute("role", "ADMIN");
                return "redirect:/admin";
            }
        } 
        
        // --- BÁC SĨ ĐĂNG NHẬP ---
        else if ("DOCTOR".equalsIgnoreCase(role) || "BAC_SI".equalsIgnoreCase(role)) {
            Optional<BacSi> bsOpt = bacSiRepository.findByEmail(email);

            if (bsOpt.isPresent()) {
                BacSi bs = bsOpt.get();
                
                if (bs.getMatKhau() != null && bs.getMatKhau().equals(password)) {
                    if (session != null) {
                        session.invalidate();
                    }
                    HttpSession newSession = request.getSession(true);
                    newSession.setAttribute("doctorUser", bs.getEmail());
                    newSession.setAttribute("role", "DOCTOR");
                    newSession.setAttribute("doctorId", bs.getId());

                    return "redirect:/bac_si";
                }
            }
        }

        model.addAttribute("error", "Tài khoản hoặc mật khẩu không chính xác!");
        return "dang_nhap_nhan_vien";
    }

    @GetMapping("/admin/logout")
    public String logoutAdmin(HttpSession session) {
        if (session != null) {
            session.removeAttribute("adminUser");
            session.removeAttribute("role");
        }
        return "redirect:/dang_nhap_nhan_vien";
    }

    @GetMapping("/bac_si/logout")
    public String logoutBacSi(HttpSession session) {
        if (session != null) {
            session.removeAttribute("doctorUser");
            session.removeAttribute("doctorId");
            session.removeAttribute("role");
        }
        return "redirect:/dang_nhap_nhan_vien";
    }

    @GetMapping("/logout")
    public String logoutAll(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/dang_nhap_nhan_vien";
    }
}