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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
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

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Load admin hashes from env vars (recommended)
    String admin1Email = System.getenv("APP_ADMIN1_EMAIL");
    String admin1Hash  = System.getenv("APP_ADMIN1_PASS_HASH");
    String admin2Email = System.getenv("APP_ADMIN2_EMAIL");
    String admin2Hash  = System.getenv("APP_ADMIN2_PASS_HASH");

    // Fallback hard-coded to the two admins you provided (no env vars required)
    if ((admin1Email == null || admin1Hash == null) && (admin2Email == null || admin2Hash == null)) {
        admin1Email = "admin1";
        admin1Hash  = encoder.encode("AdminPass123!");
        admin2Email = "admin2";
        admin2Hash  = encoder.encode("AdminPass456!");
    }

    // Check admin1
    if (admin1Email != null && admin1Email.equalsIgnoreCase(email) && admin1Hash != null && encoder.matches(matKhau, admin1Hash)) {
        session.setAttribute("userLuuSinh", null);
        session.setAttribute("userEmail", admin1Email);
        session.setAttribute("userName", "Administrator");
        session.setAttribute("userRole", "ADMIN");
        return "redirect:/admin";
    }

    // Check admin2
    if (admin2Email != null && admin2Email.equalsIgnoreCase(email) && admin2Hash != null && encoder.matches(matKhau, admin2Hash)) {
        session.setAttribute("userLuuSinh", null);
        session.setAttribute("userEmail", admin2Email);
        session.setAttribute("userName", "Administrator");
        session.setAttribute("userRole", "ADMIN");
        return "redirect:/admin";
    }

    Optional<NguoiDungBenhNhan> userOpt = khoNguoiDungBenhNhan.findByEmail(email);

 if (userOpt.isPresent() && userOpt.get().getMatKhau().equals(matKhau)) {
 NguoiDungBenhNhan user = userOpt.get();
 session.setAttribute("userLuuSinh", user);
session.setAttribute("userId", user.getId());
 session.setAttribute("userEmail", user.getEmail());
 session.setAttribute("userName", user.getHoTen());
 session.setAttribute("userRole", "BENH_NHAN");
 return "redirect:/";
 }

 Optional<BacSi> bacSiOpt = khoBacSi.findByEmail(email);

 if (bacSiOpt.isPresent() && bacSiOpt.get().getMatKhau().equals(matKhau)) {
 BacSi bs = bacSiOpt.get();
 session.setAttribute("userLuuSinh", bs);
 session.setAttribute("doctorId", bs.getId()); 
 session.setAttribute("bacSiId", bs.getId()); 
 session.setAttribute("userEmail", bs.getEmail());
 session.setAttribute("userName", bs.getHoTen());

 String vaiTro = bs.getVaiTro() != null ? bs.getVaiTro().toUpperCase() : "BAC_SI";
 session.setAttribute("userRole", vaiTro);

    if ("ADMIN".equals(vaiTro)) {
    return "redirect:/admin";
    } else {
 return "redirect:/bac-si/dashboard";
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

// Khớp với thuộc tính hoTenBenhNhan và tenBenhNhan trong LichKham.java
newLich.setHoTenBenhNhan(fullName);
newLich.setTenBenhNhan(fullName);
newLich.setSoDienThoai(phone);

String userEmail = (email != null && !email.isBlank()) ? email : (String) session.getAttribute("userEmail");
 newLich.setEmail(userEmail);

            // Nếu có doctorId thì gán, đồng thời cố gắng lấy tên bác sĩ nếu cần
            if (doctorId != null) {
                newLich.setBacSiId(doctorId);
                // lấy tên bác sĩ để hiển thị (không bắt buộc)
                bacSiService.getBacSiById(doctorId.intValue()).ifPresent(bs -> newLich.setTenBacSi(bs.getHoTen()));
            }

            newLich.setNgayKham(appointmentDate);
            // form gửi appointmentTime
            newLich.setGioKham(appointmentTime != null ? appointmentTime : "");
            newLich.setGhiChu(symptoms != null ? symptoms : "");
newLich.setTrangThai("CHO_XAC_NHAN");
newLich.setNgayDat(LocalDateTime.now());
newLich.setNgayTao(LocalDateTime.now());

            // Gán chuyên khoa nếu gửi departmentId (không bắt buộc)
            if (departmentId != null) {
                ChuyenKhoa ck = chuyenKhoaService.getChuyenKhoaById(departmentId).orElse(null);
                if (ck != null) {
                    newLich.setChuyenKhoaId(ck.getId().longValue());
                }
            }

 // Gán bệnh nhân nếu đang đăng nhập (JPA sẽ tự động tự ánh xánh ID thông qua NguoiDungBenhNhan)
// Gán bệnh nhân nếu đang đăng nhập
 Object userIdObj = session.getAttribute("userId");
if (userIdObj != null) {
Long userId = Long.valueOf(userIdObj.toString());
NguoiDungBenhNhan bn = new NguoiDungBenhNhan();
 bn.setId(userId);
newLich.setBenhNhan(bn);
 }

// Lưu vào CSDL
            lichKhamService.taoLichKham(newLich);

            // Nếu người dùng đã đăng nhập là bệnh nhân, chuyển sang lịch sử; nếu không, trở lại trang đặt lịch và hiển thị thông báo
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

    // API để xử lý đặt lịch qua AJAX, trả về JSON (giữ luồng ở trang đặt lịch)
    @PostMapping("/api/dat-lich")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiDatLich(
            @RequestParam(name = "fullName", required = false) String fullName,
            @RequestParam(name = "phone", required = false) String phone,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "departmentId", required = false) Integer departmentId,
            @RequestParam(name = "doctorId", required = false) Long doctorId,
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

            String userEmail = (email != null && !email.isBlank()) ? email : (String) session.getAttribute("userEmail");
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
                if (ck != null) newLich.setChuyenKhoaId(ck.getId().longValue());
            }

            Object userIdObj = session.getAttribute("userId");
            if (userIdObj != null) {
                Long userId = Long.valueOf(userIdObj.toString());
                NguoiDungBenhNhan bn = new NguoiDungBenhNhan();
                bn.setId(userId);
                newLich.setBenhNhan(bn);
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

 if (userEmail == null || (roleObj != null && !"BENH_NHAN".equalsIgnoreCase(roleObj.toString()))) {
 return "redirect:/dang-nhap";
 }

    List<LichKham> dsLichKham = lichKhamService.getLichByEmail(userEmail);
    model.addAttribute("dsLichKham", dsLichKham);
    model.addAttribute("danhSachLich", dsLichKham);
    model.addAttribute("userEmail", userEmail);

    // Template file in resources is named `lich_su_dang_ky.html` — return that view name
    return "lich_su_dang_ky";
}
}