package com.phongkham.booking.controller;

import com.phongkham.booking.entity.BacSi;
import com.phongkham.booking.entity.ChuyenKhoa;
import com.phongkham.booking.entity.LichKham;
import com.phongkham.booking.entity.NguoiDungBenhNhan;
import com.phongkham.booking.repository.BacSiRepository;
import com.phongkham.booking.repository.KhoNguoiDungBenhNhan;
import com.phongkham.booking.service.BacSiService;
import com.phongkham.booking.service.ChuyenKhoaService;
import com.phongkham.booking.service.LichKhamService;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
public class WebController {

    @Autowired
    private KhoNguoiDungBenhNhan khoNguoiDungBenhNhan;

    @Autowired
    private BacSiRepository khoBacSi;

    @Autowired
    private LichKhamService lichKhamService;

    @Autowired
    private ChuyenKhoaService chuyenKhoaService;

@Autowired
    private BacSiService bacSiService;

@Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin1.username:admin1}")
    private String admin1User;
    @Value("${admin1.password:AdminPass123!}")
    private String admin1Pass;

    @Value("${admin2.username:admin2}")
    private String admin2User;
    @Value("${admin2.password:AdminPass456!}")
    private String admin2Pass;

    // =========================================================
    // 1. CÁC TRANG GIAO DIỆN CHUNG & TĨNH
    // =========================================================
    @GetMapping("/")
    public String index(Model model) {
        List<ChuyenKhoa> dsChuyenKhoa = chuyenKhoaService.getAllChuyenKhoa();
        model.addAttribute("danhSachChuyenKhoa", dsChuyenKhoa);
        return "index";
    }

    @GetMapping({"/gioi_thieu", "/gioi-thieu"})
    public String trangGioiThieu() {
        return "gioi_thieu";
    }

    // =========================================================
    // 2. ĐĂNG KÝ & ĐĂNG NHẬP BỆNH NHÂN / BÁC SĨ / ADMIN
    // =========================================================
    @GetMapping({"/dang-nhap", "/dang_nhap"})
    public String trangDangNhap() {
        return "dang_nhap";
    }

    @PostMapping({"/dang-nhap", "/dang_nhap"})
    public String xuLyDangNhap(@RequestParam String email,
                               @RequestParam String matKhau,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

// =============================================
        // 1. KIỂM TRA TÀI KHOẢN ADMIN (cấu hình cố định)
        // =============================================
        boolean isAdmin1 = admin1User.equalsIgnoreCase(email) && admin1Pass.equals(matKhau);
        boolean isAdmin2 = admin2User.equalsIgnoreCase(email) && admin2Pass.equals(matKhau);

        if (isAdmin1 || isAdmin2) {
            session.invalidate();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("adminUser", email);
            newSession.setAttribute("userEmail", email);
            newSession.setAttribute("userName", "Administrator");
            newSession.setAttribute("userRole", "ADMIN");
            newSession.setAttribute("role", "ADMIN");
            return "redirect:/admin";
        }

        // 2. ƯU TIÊN KIỂM TRA BỆNH NHÂN TRƯỚC
        Optional<NguoiDungBenhNhan> userOpt = khoNguoiDungBenhNhan.findByEmail(email);
        if (userOpt.isPresent() && userOpt.get().getMatKhau().equals(matKhau)) {
            NguoiDungBenhNhan user = userOpt.get();
            session.invalidate();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            HttpSession newSession = request.getSession(true);

newSession.setAttribute("userLuuSinh", user);
            newSession.setAttribute("userId", user.getId());
            newSession.setAttribute("userEmail", user.getEmail());
            newSession.setAttribute("userName", user.getHoTen());
            newSession.setAttribute("userPhone", user.getSoDienThoai());
            newSession.setAttribute("userRole", "BENH_NHAN");
            return "redirect:/";
        }

// 2. SAU ĐÓ MỚI KIỂM TRA BÁC SĨ
        Optional<BacSi> bacSiOpt = khoBacSi.findByEmail(email);
        if (bacSiOpt.isPresent() &&
                (passwordEncoder.matches(matKhau, bacSiOpt.get().getMatKhau())
                 || bacSiOpt.get().getMatKhau().equals(matKhau))) {
            BacSi bs = bacSiOpt.get();
            session.invalidate();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            HttpSession newSession = request.getSession(true);

newSession.setAttribute("userLuuSinh", bs);
            newSession.setAttribute("doctorId", bs.getId()); 
            newSession.setAttribute("bacSiId", bs.getId()); 
            newSession.setAttribute("doctorUser", bs.getEmail());
            newSession.setAttribute("userEmail", bs.getEmail());
            newSession.setAttribute("userName", bs.getHoTen());

            String vaiTro = bs.getVaiTro() != null ? bs.getVaiTro().toUpperCase() : "BAC_SI";
            newSession.setAttribute("userRole", vaiTro);
            newSession.setAttribute("role", "DOCTOR");

            if ("ADMIN".equals(vaiTro)) {
                return "redirect:/admin";
            } else {
                return "redirect:/bac_si";
            }
        }

        redirectAttributes.addFlashAttribute("loi", "Email hoặc mật khẩu không chính xác!");
        return "redirect:/dang-nhap";
    }

    @GetMapping({"/dang-ky", "/dang_ky"})
    public String trangDangKy() {
        return "dang_ky";
    }

    @PostMapping({"/dang-ky", "/dang_ky"})
    public String xuLyDangKy(
            @RequestParam String hoTen,
            @RequestParam String email,
            @RequestParam String soDienThoai,
            @RequestParam String matKhau,
            RedirectAttributes redirectAttributes) {

        if (khoNguoiDungBenhNhan.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("loi", "Email này đã được sử dụng!");
            return "redirect:/dang-ky";
        }

        NguoiDungBenhNhan user = new NguoiDungBenhNhan();
        user.setHoTen(hoTen);
        user.setEmail(email);
        user.setSoDienThoai(soDienThoai);
        user.setMatKhau(matKhau);
        user.setNgayTao(LocalDateTime.now());

        khoNguoiDungBenhNhan.save(user);

        redirectAttributes.addFlashAttribute("thongBao", "Đăng ký thành công! Vui lòng đăng nhập.");
        return "redirect:/dang-nhap";
    }

    @GetMapping({"/dang-xuat", "/dang_xuat"})
    public String dangXuat(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping({"/doi_ngu_bac_si", "/doi-ngu-bac-si"})
    public String doiNguBacSi(Model model) {
        List<ChuyenKhoa> dsChuyenKhoa = chuyenKhoaService.getAllChuyenKhoa(); 
        model.addAttribute("danhSachChuyenKhoa", dsChuyenKhoa);
        return "doi_ngu_bac_si";
    }

    // =========================================================
    // 3. ĐẶT LỊCH KHÁM & LƯU VÀO CSDL
    // =========================================================
    @GetMapping({"/dat_lich_kham_benh", "/dat-lich-kham-benh", "/dat-lich-kham"})
    public String datLichKhamBenh(@RequestParam(name = "doctor", required = false) String doctorName, Model model) {
        model.addAttribute("selectedDoctor", doctorName);
        model.addAttribute("danhSachBacSi", bacSiService.getAllBacSi());

        List<ChuyenKhoa> dsKhoa = chuyenKhoaService.getAllChuyenKhoa();
        model.addAttribute("dsChuyenKhoa", dsKhoa);
        model.addAttribute("danhSachChuyenKhoa", dsKhoa);

        return "dat_lich_kham_benh"; 
    }

    @PostMapping({"/dat-lich/luu", "/dat-lich-kham"})
    public String luuLichKham(
            @RequestParam(name = "fullName", required = false) String fullName,
            @RequestParam(name = "phone", required = false) String phone,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "departmentId", required = false) Integer departmentId,
            @RequestParam(name = "doctorId", required = false) Long doctorId,
            @RequestParam(name = "appointmentDate", required = false) String appointmentDate,
            @RequestParam(name = "appointmentTime", required = false) String appointmentTime,
            @RequestParam(name = "symptoms", required = false) String symptoms,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            LichKham newLich = new LichKham();

newLich.setHoTenBenhNhan(fullName);
            newLich.setTenBenhNhan(fullName);
            newLich.setSoDienThoai(phone);

            // Nếu bệnh nhân đã đăng nhập thì ưu tiên dùng email + SĐT từ tài khoản
            // để lịch sử đăng ký tra cứu được chính xác
            Object sessRole = session.getAttribute("userRole");
            String sessEmail = (String) session.getAttribute("userEmail");
            String userEmail;
            if ("BENH_NHAN".equalsIgnoreCase(sessRole != null ? sessRole.toString() : "") && sessEmail != null && !sessEmail.isBlank()) {
                userEmail = sessEmail;
                String sessPhone = (String) session.getAttribute("userPhone");
                if (sessPhone != null && !sessPhone.isBlank()) {
                    newLich.setSoDienThoai(sessPhone);
                }
            } else {
                userEmail = (email != null && !email.isBlank()) ? email : sessEmail;
            }
            newLich.setEmail(userEmail);

            if (doctorId != null) {
                newLich.setBacSiId(doctorId);
                bacSiService.getBacSiById(doctorId.intValue()).ifPresent(bs -> newLich.setTenBacSi(bs.getHoTen()));
            }

            newLich.setNgayKham(appointmentDate);
            newLich.setGioKham(appointmentTime != null ? appointmentTime : "");
            newLich.setGhiChu(symptoms != null ? symptoms : "");
            newLich.setTrangThai("CHO_XAC_NHAN");
            newLich.setNgayDat(LocalDateTime.now());
            newLich.setNgayTao(LocalDateTime.now());

            if (departmentId != null) {
                ChuyenKhoa ck = chuyenKhoaService.getChuyenKhoaById(departmentId).orElse(null);
                if (ck != null) {
                    newLich.setChuyenKhoaId(ck.getId().longValue());
                }
            }

            Object userIdObj = session.getAttribute("userId");
            if (userIdObj != null) {
                Long userId = Long.valueOf(userIdObj.toString());
                NguoiDungBenhNhan bn = new NguoiDungBenhNhan();
                bn.setId(userId);
                newLich.setBenhNhan(bn);
            }

            lichKhamService.taoLichKham(newLich);

            Object roleObj = session.getAttribute("userRole");
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đặt lịch thành công! Vui lòng chờ bác sĩ xử lý.");
            if (roleObj != null && "BENH_NHAN".equalsIgnoreCase(roleObj.toString())) {
                return "redirect:/lich-su-kham";
            } else {
                return "redirect:/dat-lich-kham";
            }

        } catch (Exception e) {
            e.printStackTrace();
            List<ChuyenKhoa> dsKhoa = chuyenKhoaService.getAllChuyenKhoa();
            model.addAttribute("dsChuyenKhoa", dsKhoa);
            model.addAttribute("danhSachChuyenKhoa", dsKhoa);
            model.addAttribute("danhSachBacSi", bacSiService.getAllBacSi());
            model.addAttribute("errorMessage", true);
            model.addAttribute("errorText", "Lỗi đặt lịch: " + e.getMessage());
            return "dat_lich_kham_benh";
        }
    }

    @PostMapping("/api/dat-lich")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiDatLich(
            @RequestParam(name = "fullName", required = false) String fullName,
            @RequestParam(name = "phone", required = false) String phone,
            @RequestParam(name = "email", required = false) String email,
@RequestParam(name = "departmentId", required = false) String departmentIdStr,
            @RequestParam(name = "doctorId", required = false) String doctorIdStr,
            @RequestParam(name = "appointmentDate", required = false) String appointmentDate,
            @RequestParam(name = "appointmentTime", required = false) String appointmentTime,
            @RequestParam(name = "symptoms", required = false) String symptoms,
            HttpSession session) {

        Map<String, Object> resp = new HashMap<>();
        try {
            LichKham newLich = new LichKham();

newLich.setHoTenBenhNhan(fullName);
            newLich.setTenBenhNhan(fullName);
            newLich.setSoDienThoai(phone);

            // Nếu bệnh nhân đã đăng nhập thì ưu tiên dùng email + SĐT từ tài khoản
            // để lịch sử đăng ký tra cứu được chính xác
            Object sessRole = session.getAttribute("userRole");
            String sessEmail = (String) session.getAttribute("userEmail");
            String userEmail;
            if ("BENH_NHAN".equalsIgnoreCase(sessRole != null ? sessRole.toString() : "") && sessEmail != null && !sessEmail.isBlank()) {
                userEmail = sessEmail;
                String sessPhone = (String) session.getAttribute("userPhone");
                if (sessPhone != null && !sessPhone.isBlank()) {
                    newLich.setSoDienThoai(sessPhone);
                }
            } else {
                userEmail = (email != null && !email.isBlank()) ? email : sessEmail;
            }
            newLich.setEmail(userEmail);

            Long doctorId = null;
            if (doctorIdStr != null && !doctorIdStr.isBlank()) {
                try {
                    doctorId = Long.valueOf(doctorIdStr.trim());
                } catch (NumberFormatException ignore) {
                    doctorId = null;
                }
            }
            if (doctorId != null) {
                // CHỈ gán quan hệ bác sĩ nếu bác sĩ thực sự tồn tại (tránh lỗi Foreign Key)
                Optional<BacSi> bsOpt = bacSiService.getBacSiById(doctorId.intValue());
                if (bsOpt.isPresent()) {
                    newLich.setBacSi(bsOpt.get());
                    newLich.setBacSiId(doctorId);
                    newLich.setTenBacSi(bsOpt.get().getHoTen());
                }
            }

            newLich.setNgayKham(appointmentDate);
            newLich.setGioKham(appointmentTime != null ? appointmentTime : "");
            newLich.setGhiChu(symptoms != null ? symptoms : "");
            newLich.setTrangThai("CHO_XAC_NHAN");
newLich.setNgayDat(LocalDateTime.now());
            newLich.setNgayTao(LocalDateTime.now());

            Integer departmentId = null;
            if (departmentIdStr != null && !departmentIdStr.isBlank()) {
                try {
                    departmentId = Integer.valueOf(departmentIdStr.trim());
                } catch (NumberFormatException ignore) {
                    departmentId = null;
                }
            }
            if (departmentId != null) {
                ChuyenKhoa ck = chuyenKhoaService.getChuyenKhoaById(departmentId).orElse(null);
                if (ck != null) newLich.setChuyenKhoaId(ck.getId().longValue());
            }

Object userIdObj = session.getAttribute("userId");
            if (userIdObj != null) {
                try {
                    Long userId = Long.valueOf(userIdObj.toString());
                    // Chỉ gán benh_nhan nếu tài khoản bệnh nhân thực sự tồn tại
                    khoNguoiDungBenhNhan.findById(userId).ifPresent(newLich::setBenhNhan);
                } catch (Exception ignore) {
                    // Nếu không gắn được benh_nhan (vd foreign key) thì bỏ qua,
                    // lịch vẫn lưu được với email + SĐT để tra cứu lịch sử.
                }
            }

            lichKhamService.taoLichKham(newLich);

            resp.put("success", true);
            resp.put("message", "Đặt lịch thành công! Vui lòng chờ bác sĩ xử lý.");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", "Lỗi khi đặt lịch: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    // =========================================================
    // 4. QUẢN LÝ LỊCH KHÁM BỆNH NHÂN (LỊCH SỬ ĐĂNG KÝ)
    // =========================================================
    @GetMapping({"/lich-su-kham", "/lich_su_kham", "/lich_su_dang_ky", "/lich-su-dang-ky"})
    public String xemLichSuKham(HttpSession session, Model model) {
        String userEmail = (String) session.getAttribute("userEmail");
        Object roleObj = session.getAttribute("userRole");

        if ((userEmail == null || userEmail.isBlank()) && (roleObj != null && !"BENH_NHAN".equalsIgnoreCase(roleObj.toString()))) {
            return "redirect:/dang-nhap";
        }

List<LichKham> dsLichKham = new ArrayList<>();
        if (userEmail != null && !userEmail.isBlank()) {
            dsLichKham.addAll(lichKhamService.getLichByEmail(userEmail.trim()));
        }

        Object userIdObj = session.getAttribute("userId");
        if (userIdObj != null) {
            try {
                Long userId = Long.valueOf(userIdObj.toString().trim());
                dsLichKham.addAll(lichKhamService.getLichByBenhNhanId(userId));
            } catch (NumberFormatException ignored) {
            }
        }

        // Bổ sung tra cứu theo SĐT đã lưu trong session (bắt các lịch đặt công khai có SĐT)
        Object userPhoneObj = session.getAttribute("userPhone");
        if (userPhoneObj != null && !userPhoneObj.toString().isBlank()) {
            dsLichKham.addAll(lichKhamService.getLichBySoDienThoai(userPhoneObj.toString().trim()));
        }

        Map<Integer, LichKham> uniqueAppointments = new LinkedHashMap<>();
        for (LichKham lich : dsLichKham) {
            if (lich != null && lich.getId() != null) {
                uniqueAppointments.putIfAbsent(lich.getId(), lich);
            }
        }

        List<LichKham> danhSachLich = new ArrayList<>(uniqueAppointments.values());
        model.addAttribute("dsLichKham", danhSachLich);
        model.addAttribute("danhSachLich", danhSachLich);
        model.addAttribute("userEmail", userEmail);

        return "lich_su_dang_ky";
    }
}