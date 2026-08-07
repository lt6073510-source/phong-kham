package com.phongkham.booking.controller;

import com.phongkham.booking.entity.*;
import com.phongkham.booking.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

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

    // Hàm tiện ích kiểm tra quyền Admin
    private boolean checkAdminSession(HttpSession session) {
        if (session == null) return false;
        String adminUser = (String) session.getAttribute("adminUser");
        String userRole = (String) session.getAttribute("userRole");
        return (adminUser != null || "ADMIN".equalsIgnoreCase(userRole));
    }

    // 1. TRANG DASHBOARD CHÍNH CỦA ADMIN
    @GetMapping
    public String adminPage(HttpSession session, Model model) {
        // ĐÃ BỔ SUNG: Kiểm tra Session Admin, nếu chưa đăng nhập sẽ đá về trang /dang_nhap
        if (!checkAdminSession(session)) {
            return "redirect:/dang_nhap";
        }

        model.addAttribute("dsChuyenKhoa", chuyenKhoaService.getAllChuyenKhoa());
        model.addAttribute("dsBacSi", bacSiService.getAllBacSi());
        model.addAttribute("dsLichKham", lichKhamService.getAllLichKham(null));
        
        model.addAttribute("chuyenKhoaMoi", new ChuyenKhoa());
        model.addAttribute("bacSiMoi", new BacSi());
        return "admin";
    }

    // --- QUẢN LÝ CHUYÊN KHOA ---
    @PostMapping("/chuyen-khoa/them")
    public String themChuyenKhoa(@ModelAttribute("chuyenKhoaMoi") ChuyenKhoa chuyenKhoa, 
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (!checkAdminSession(session)) return "redirect:/dang_nhap";

        try {
            String ten = chuyenKhoa.getTenChuyenKhoa();
            String moTa = chuyenKhoa.getMoTa();

            if (ten == null || ten.isBlank()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tên chuyên khoa không được để trống!");
                return "redirect:/admin#tab-chuyenkhoa";
            }

            if (chuyenKhoa.getTen_chuyen_khoa() == null || chuyenKhoa.getTen_chuyen_khoa().isBlank()) {
                chuyenKhoa.setTen_chuyen_khoa(ten + " Chuyên Sâu");
            }

            if (chuyenKhoa.getHinhAnh() == null || chuyenKhoa.getHinhAnh().isBlank()) {
                chuyenKhoa.setHinhAnh("/anh_bia.jpeg");
            }

            if (chuyenKhoa.getPhu_de_banner() == null || chuyenKhoa.getPhu_de_banner().isBlank()) {
                chuyenKhoa.setPhu_de_banner("Trung tâm tiếp nhận, chẩn đoán và điều trị " + ten + " toàn diện tại Phòng khám.");
            }

            if (chuyenKhoa.getDoan_gioi_thieu() == null || chuyenKhoa.getDoan_gioi_thieu().isBlank()) {
                chuyenKhoa.setDoan_gioi_thieu("<strong>" + ten + "</strong> chuyên tiếp nhận, chẩn đoán và điều trị toàn diện các bệnh lý. " + (moTa != null ? moTa : ""));
            }

            if (chuyenKhoa.getTieu_de_hang_muc() == null || chuyenKhoa.getTieu_de_hang_muc().isBlank()) {
                chuyenKhoa.setTieu_de_hang_muc("Các hạng mục thăm khám và điều trị chính");
            }

            if (chuyenKhoa.getLoi_ket() == null || chuyenKhoa.getLoi_ket().isBlank()) {
                chuyenKhoa.setLoi_ket(ten + " đóng vai trò quan trọng trong việc chăm sóc và bảo vệ sức khỏe toàn diện cho bệnh nhân.");
            }

            if (chuyenKhoa.getMaKhoa() == null || chuyenKhoa.getMaKhoa().isBlank()) {
                String maKhoa = ten.toLowerCase()
                        .trim()
                        .replaceAll("[áàảãạâấầẩẫậăắằẳẵặ]", "a")
                        .replaceAll("[đ]", "d")
                        .replaceAll("[éèẻẽẹêếềểễệ]", "e")
                        .replaceAll("[óòỏõọôốồổỗộơớờởỡợ]", "o")
                        .replaceAll("[úùủũụưứừửữự]", "u")
                        .replaceAll("[íìỉĩị]", "i")
                        .replaceAll("[ýỳỷỹỵ]", "y")
                        .replaceAll("[^a-z0-9]", "-")
                        .replaceAll("-+", "-");
                chuyenKhoa.setMa_khoa(maKhoa);
            }

            if (chuyenKhoa.getDanh_sach_hang_muc() == null || chuyenKhoa.getDanh_sach_hang_muc().isEmpty()) {
                List<com.phongkham.booking.entity.HangMuc> defaultHangMuc = new ArrayList<>();
                defaultHangMuc.add(new com.phongkham.booking.entity.HangMuc("🩺", "Thăm khám & Tầm soát", "Khám lâm sàng và đánh giá tổng quát các triệu chứng ban đầu."));
                defaultHangMuc.add(new com.phongkham.booking.entity.HangMuc("💊", "Điều trị chuyên khoa", "Đưa ra phác đồ điều trị nội khoa chuẩn y khoa và theo dõi sát sao."));
                chuyenKhoa.setDanh_sach_hang_muc(defaultHangMuc);
            }

            chuyenKhoaService.taoChuyenKhoa(chuyenKhoa);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm chuyên khoa thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể thêm chuyên khoa: " + e.getMessage());
        }
        return "redirect:/admin#tab-chuyenkhoa";
    }

    @GetMapping("/chuyen-khoa/xoa/{id}")
    public String xoaChuyenKhoa(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAdminSession(session)) return "redirect:/dang_nhap";

        try {
            chuyenKhoaService.xoaChuyenKhoa(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa chuyên khoa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chuyên khoa đang chứa bác sĩ, không thể xóa!");
        }
        return "redirect:/admin#tab-chuyenkhoa";
    }

    // --- QUẢN LÝ BÁC SĨ ---

    @PostMapping("/bac-si/cap-nhat")
    @Transactional
    public String capNhatHoSoBacSi(
            @RequestParam("id") Integer id,
            @RequestParam("chuyenKhoaId") Integer chuyenKhoaId,
            @RequestParam("hocVi") String hocVi,
            @RequestParam(value = "moTa", required = false) String moTa,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!checkAdminSession(session)) return "redirect:/dang_nhap";

        try {
            Optional<BacSi> bacSiOpt = bacSiService.getBacSiById(id);
            
            if (bacSiOpt.isPresent()) {
                BacSi bacSi = bacSiOpt.get();
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

    @GetMapping("/bac-si/xoa/{id}")
    public String xoaBacSi(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAdminSession(session)) return "redirect:/dang_nhap";

        try {
            bacSiService.xoaBacSi(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa bác sĩ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa bác sĩ này (có thể do đã có lịch khám trong hệ thống)!");
        }
        return "redirect:/admin#tab-bacsi";
    }

    /**
     * CẤP TÀI KHOẢN VÀ HỒ SƠ BÁC SĨ MỚI (Có mã hóa BCrypt)
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
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!checkAdminSession(session)) return "redirect:/dang_nhap";

        try {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            String cleanEmail = (email != null && !email.isBlank()) ? email.trim() : (tenDangNhap.trim() + "@phongkham.com");
            String cleanPhone = (soDienThoai != null && !soDienThoai.isBlank()) ? soDienThoai.trim() : null;
            String cleanHoTen = (hoTen != null && !hoTen.isBlank()) ? hoTen.trim() : "BS. " + tenDangNhap.trim();
            String cleanHocVi = (hocVi != null && !hocVi.isBlank()) ? hocVi.trim() : "Bác sĩ";

            BacSi bacSi = new BacSi();
            bacSi.setHoTen(cleanHoTen);
            bacSi.setEmail(cleanEmail);
            
            // ĐÃ SỬA: Mã hóa BCrypt mật khẩu khi tạo mới tài khoản Bác sĩ
            bacSi.setMatKhau(encoder.encode(matKhau));
            
            bacSi.setSoDienThoai(cleanPhone);
            bacSi.setHocVi(cleanHocVi);
            bacSi.setVaiTro("BAC_SI");
            bacSi.setTrangThai("HOAT_DONG");

            if (chuyenKhoaId != null) {
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
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        if (!checkAdminSession(session)) return "redirect:/dang_nhap";

        try {
            lichKhamService.xoaLichKham(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa/hủy lịch khám thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Thao tác thất bại!");
        }
        return "redirect:/admin#tab-lichkham";
    }

    @GetMapping("/lich-kham/xoa/{id}")
    public String xoaLichKham(@PathVariable("id") Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAdminSession(session)) return "redirect:/dang_nhap";

        try {
            lichKhamService.xoaLichKham(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa lịch khám thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa lịch khám này!");
        }
        return "redirect:/admin#tab-lichkham";
    }
}