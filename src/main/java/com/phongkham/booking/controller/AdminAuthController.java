package com.phongkham.booking.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.phongkham.booking.entity.BacSi;
import com.phongkham.booking.repository.BacSiRepository;

import java.util.Optional;

@Controller
public class AdminAuthController {

    private final BacSiRepository bacSiRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminAuthController(BacSiRepository bacSiRepository, PasswordEncoder passwordEncoder) {
        this.bacSiRepository = bacSiRepository;
        this.passwordEncoder = passwordEncoder;
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
    // TRANG ĐĂNG NHẬP CHUNG CHUYỂN HƯỚNG TỰ ĐỘNG
    // ==========================================

    @GetMapping({"/login", "/dang_nhap", })
    public String trangDangNhap(HttpSession session) {
        if (session.getAttribute("adminUser") != null) {
            return "redirect:/admin";
        }
        if (session.getAttribute("doctorUser") != null) {
            return "redirect:/bac_si";
        }
        return "dang_nhap";
    }

    @PostMapping({"/dang_nhap", })
    public String xuLyDangNhapTuDong(@RequestParam("email") String email,
                                     @RequestParam(value = "matKhau", required = false) String matKhauVN,
                                     @RequestParam(value = "password", required = false) String matKhauEN,
                                     HttpServletRequest request,
                                     HttpSession session,
                                     Model model) {

        // Lấy mật khẩu linh hoạt từ trường "matKhau" hoặc "password"
        String password = (matKhauVN != null && !matKhauVN.trim().isEmpty()) ? matKhauVN : matKhauEN;

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            model.addAttribute("loi", "Vui lòng điền đầy đủ email và mật khẩu!");
            return "dang_nhap";
        }

        // --------------------------------------------------
        // 1. NHẬN DIỆN VÀ XỬ LÝ TÀI KHOẢN ADMIN
        // --------------------------------------------------
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

        // --------------------------------------------------
        // 2. NHẬN DIỆN VÀ XỬ LÝ TÀI KHOẢN BÁC SĨ
        // --------------------------------------------------
        Optional<BacSi> bsOpt = bacSiRepository.findByEmail(email);

        if (bsOpt.isPresent()) {
            BacSi bs = bsOpt.get();
            boolean isPasswordCorrect = false;

            if (bs.getMatKhau() != null) {
                // Kiểm tra bằng PasswordEncoder mã hóa BCrypt hoặc chuỗi chưa mã hóa
                if (passwordEncoder.matches(password, bs.getMatKhau()) || bs.getMatKhau().equals(password)) {
                    isPasswordCorrect = true;
                }
            }

            if (isPasswordCorrect) {
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

        // --------------------------------------------------
        // 3. KHÔNG KHỚP ADMIN HOẶC BÁC SĨ -> BÁO LỖI
        // --------------------------------------------------
        model.addAttribute("loi", "Tài khoản hoặc mật khẩu không chính xác!");
        return "dang_nhap";
    }

    // ==========================================
    // CÁC ENDPOINT ĐĂNG XUẤT
    // ==========================================

    @GetMapping("/admin/logout")
    public String logoutAdmin(HttpSession session) {
        if (session != null) {
            session.removeAttribute("adminUser");
            session.removeAttribute("role");
        }
        return "redirect:/dang_nhap";
    }

    @GetMapping("/bac_si/logout")
    public String logoutBacSi(HttpSession session) {
        if (session != null) {
            session.removeAttribute("doctorUser");
            session.removeAttribute("doctorId");
            session.removeAttribute("role");
        }
        return "redirect:/dang_nhap";
    }

    @GetMapping("/logout")
    public String logoutAll(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/dang_nhap";
    }
}