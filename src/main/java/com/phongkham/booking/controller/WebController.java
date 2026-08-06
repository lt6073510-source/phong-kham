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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
 return "redirect:/admin/dashboard";
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
@RequestParam(name = "department", required = false) String department,
 @RequestParam(name = "doctorName", required = false) String doctorName,
@RequestParam(name = "doctorId", required = false) Long doctorId,
@RequestParam(name = "appointmentDate", required = false) String appointmentDate,
 @RequestParam(name = "timeSlot", required = false) String timeSlot,
@RequestParam(name = "note", required = false) String note,
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

newLich.setTenBacSi(doctorName);
 newLich.setNgayKham(appointmentDate);
newLich.setGioKham(timeSlot);
newLich.setGhiChu(note);
newLich.setTrangThai("CHO_XAC_NHAN");
newLich.setNgayDat(LocalDateTime.now());
newLich.setNgayTao(LocalDateTime.now());

// Gán bác sĩ theo helper method
 if (doctorId != null) {
newLich.setBacSiId(doctorId);
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

redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đặt lịch thành công! Vui lòng chờ bác sĩ xử lý.");
 return "redirect:/lich-su-kham";

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

 return "lichsukham";
}
}