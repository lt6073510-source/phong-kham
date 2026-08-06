package com.phongkham.booking.controller;

import com.phongkham.booking.entity.*;
import com.phongkham.booking.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    private final ChuyenKhoaService chuyenKhoaService;
    private final BacSiService bacSiService;
    private final LichKhamService lichKhamService;

    public AdminViewController(ChuyenKhoaService chuyenKhoaService, 
                               BacSiService bacSiService, 
                               LichKhamService lichKhamService) {
        this.chuyenKhoaService = chuyenKhoaService;
        this.bacSiService = bacSiService;
        this.lichKhamService = lichKhamService;
    }

    // 1. TRANG DASHBOARD CHÍNH CỦA ADMIN
    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("dsChuyenKhoa", chuyenKhoaService.getAllChuyenKhoa());
        model.addAttribute("dsBacSi", bacSiService.getAllBacSi());
        model.addAttribute("dsLichKham", lichKhamService.getAllLichKham(null));
        
        model.addAttribute("chuyenKhoaMoi", new ChuyenKhoa());
        model.addAttribute("bacSiMoi", new BacSi());
        return "admin";
    }

    // --- QUẢN LÝ CHUYÊN KHOA ---
    @PostMapping("/chuyen-khoa/them")
    public String themChuyenKhoa(@ModelAttribute("chuyenKhoaMoi") ChuyenKhoa chuyenKhoa, RedirectAttributes redirectAttributes) {
        try {
            chuyenKhoaService.taoChuyenKhoa(chuyenKhoa);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm chuyên khoa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể thêm chuyên khoa!");
        }
        return "redirect:/admin#tab-chuyenkhoa";
    }

    @GetMapping("/chuyen-khoa/xoa/{id}")
    public String xoaChuyenKhoa(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            chuyenKhoaService.xoaChuyenKhoa(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa chuyên khoa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chuyên khoa đang chứa bác sĩ, không thể xóa!");
        }
        return "redirect:/admin#tab-chuyenkhoa";
    }

    // --- QUẢN LÝ BÁC SĨ ---

    /**
     * CẬP NHẬT HỒ SƠ BÁC SĨ
     */
    @PostMapping("/bac-si/cap-nhat")
    @Transactional
    public String capNhatHoSoBacSi(
            @RequestParam("id") Integer id,
            @RequestParam("chuyenKhoaId") Integer chuyenKhoaId,
            @RequestParam("hocVi") String hocVi,
            @RequestParam(value = "moTa", required = false) String moTa,
            RedirectAttributes redirectAttributes) {
        try {
            Optional<BacSi> bacSiOpt = bacSiService.getBacSiById(id);
            
            if (bacSiOpt.isPresent()) {
                BacSi bacSi = bacSiOpt.get();
                // Sửa: Lấy ChuyenKhoa từ Optional hoặc null bằng .orElse(null)
                ChuyenKhoa chuyenKhoa = chuyenKhoaService.getChuyenKhoaById(chuyenKhoaId).orElse(null);
                
                bacSi.setChuyenKhoa(chuyenKhoa);
                bacSi.setHocVi(hocVi);
                bacSi.setMoTa(moTa);
                
                bacSiService.luuBacSi(bacSi);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ bác sĩ thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin bác sĩ!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật hồ sơ bác sĩ: " + e.getMessage());
        }

        return "redirect:/admin#tab-bacsi";
    }

    /**
     * XÓA BÁC SĨ
     */
    @GetMapping("/bac-si/xoa/{id}")
    public String xoaBacSi(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            bacSiService.xoaBacSi(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa bác sĩ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa bác sĩ này (có thể do đã có lịch khám trong hệ thống)!");
        }
        return "redirect:/admin#tab-bacsi";
    }

    /**
     * CẤP TÀI KHOẢN VÀ HỒ SƠ BÁC SĨ MỚI (Lưu trực tiếp vào bảng bacsi)
     */
    @PostMapping("/cap-tai-khoan-bac-si")
    @Transactional
    public String capTaiKhoanBacSi(
            @RequestParam("tenDangNhap") String tenDangNhap,
            @RequestParam("matKhau") String matKhau,
            @RequestParam(value = "hoTen", required = false) String hoTen,
            @RequestParam(value = "hocVi", required = false) String hocVi,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "soDienThoai", required = false) String soDienThoai,
            @RequestParam(value = "chuyenKhoaId", required = false) Integer chuyenKhoaId,
            RedirectAttributes redirectAttributes) {
        try {
            String cleanEmail = (email != null && !email.isBlank()) ? email.trim() : (tenDangNhap.trim() + "@phongkham.com");
            String cleanPhone = (soDienThoai != null && !soDienThoai.isBlank()) ? soDienThoai.trim() : null;
            String cleanHoTen = (hoTen != null && !hoTen.isBlank()) ? hoTen.trim() : "BS. " + tenDangNhap.trim();
            String cleanHocVi = (hocVi != null && !hocVi.isBlank()) ? hocVi.trim() : "Bác sĩ";

            BacSi bacSi = new BacSi();
            bacSi.setHoTen(cleanHoTen);
            bacSi.setEmail(cleanEmail);
            bacSi.setMatKhau(matKhau);
            bacSi.setSoDienThoai(cleanPhone);
            bacSi.setHocVi(cleanHocVi);
            bacSi.setVaiTro("BAC_SI");
            bacSi.setTrangThai("HOAT_DONG");

            if (chuyenKhoaId != null) {
                // Sửa: Lấy ChuyenKhoa từ Optional hoặc null bằng .orElse(null)
                ChuyenKhoa chuyenKhoa = chuyenKhoaService.getChuyenKhoaById(chuyenKhoaId).orElse(null);
                bacSi.setChuyenKhoa(chuyenKhoa);
            }

            bacSiService.luuBacSi(bacSi);
            redirectAttributes.addFlashAttribute("successMessage", "Cấp tài khoản Bác sĩ thành công!");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Cấp tài khoản thất bại! Email/SĐT có thể đã tồn tại.");
        }

        return "redirect:/admin#tab-taikhoan";
    }

    // --- QUẢN LÝ LỊCH KHÁM ---
    @PostMapping("/lich-kham/huy")
    public String adminHuyLichKham(@RequestParam("lichKhamId") Integer id, 
                                   @RequestParam(value = "lyDoHuy", required = false) String lyDoHuy,
                                   RedirectAttributes redirectAttributes) {
        try {
            lichKhamService.xoaLichKham(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa/hủy lịch khám thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Thao tác thất bại!");
        }
        return "redirect:/admin#tab-lichkham";
    }

    @GetMapping("/lich-kham/xoa/{id}")
    public String xoaLichKham(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            lichKhamService.xoaLichKham(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa lịch khám thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa lịch khám này!");
        }
        return "redirect:/admin#tab-lichkham";
    }
}