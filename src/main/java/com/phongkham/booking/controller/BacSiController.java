package com.phongkham.booking.controller;

import com.phongkham.booking.entity.BacSi;
import com.phongkham.booking.entity.LichKham;
import com.phongkham.booking.entity.NguoiDungBenhNhan;
import com.phongkham.booking.repository.KhoNguoiDungBenhNhan;
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
    private final KhoNguoiDungBenhNhan khoNguoiDungBenhNhan;

    public BacSiController(BacSiService bacSiService, LichKhamService lichKhamService,
                           KhoNguoiDungBenhNhan khoNguoiDungBenhNhan) {
        this.bacSiService = bacSiService;
        this.lichKhamService = lichKhamService;
        this.khoNguoiDungBenhNhan = khoNguoiDungBenhNhan;
    }

    // Hàm tiện ích lấy email Bác sĩ an toàn từ Session
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
        
        // Nếu không có email trong session, kiểm tra tiếp xem có lưu trực tiếp object Bác sĩ hay ID không
        if (doctorEmail == null) {
            Object bsObj = session.getAttribute("userLuuSinh");
            if (bsObj instanceof BacSi) {
                doctorEmail = ((BacSi) bsObj).getEmail();
            }
        }

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
            session.setAttribute("userEmail", bs.getEmail());

            model.addAttribute("doctorId", bs.getId());
            model.addAttribute("tenBacSi", bs.getHoTen());
            model.addAttribute("departmentName", bs.getChuyenKhoa() != null ? bs.getChuyenKhoa().getTenChuyenKhoa() : "Chuyên khoa: Chưa cập nhật");

// Xử lý lấy lịch khám an toàn bằng try-catch (BẢNG DANH SÁCH: lấy TẤT CẢ lịch của bác sĩ)
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

            // Thống kê 4 ô "TỔNG QUAN HÔM NAY" (CHỈ đếm lịch của HÔM NAY theo đúng bác sĩ)
            List<LichKham> lichHomNay = new ArrayList<>();
            try {
                if (bs.getId() != null) {
                    List<LichKham> list = lichKhamService.getLichHomNayByBacSi(bs.getId().intValue());
                    if (list != null) {
                        lichHomNay = list;
                    }
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi truy vấn lịch hôm nay: " + e.getMessage());
            }
            final List<LichKham> finalLich = lichHomNay;
            model.addAttribute("countPending", finalLich.stream()
                    .filter(l -> l != null && (l.getTrangThai() == null || "CHO_XAC_NHAN".equalsIgnoreCase(l.getTrangThai()) || "PENDING".equalsIgnoreCase(l.getTrangThai())))
                    .count());
            
            model.addAttribute("countConfirmed", finalLich.stream()
                    .filter(l -> l != null && l.getTrangThai() != null && ("DA_XAC_NHAN".equalsIgnoreCase(l.getTrangThai()) || "CONFIRMED".equalsIgnoreCase(l.getTrangThai())))
                    .count());
            
            model.addAttribute("countCompleted", finalLich.stream()
                    .filter(l -> l != null && l.getTrangThai() != null && ("HOAN_THANH".equalsIgnoreCase(l.getTrangThai()) || "COMPLETED".equalsIgnoreCase(l.getTrangThai())))
                    .count());
            
            model.addAttribute("countCancelled", finalLich.stream()
                    .filter(l -> l != null && l.getTrangThai() != null && ("DA_HUY".equalsIgnoreCase(l.getTrangThai()) || "CANCELLED".equalsIgnoreCase(l.getTrangThai())))
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
                                  @RequestParam(value = "patientKey", required = false) String patientKey,
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
        model.addAttribute("selectedPatientKey", patientKey);

        // Xác định ID bác sĩ từ session
        Integer doctorId = null;
        Object bsId = session.getAttribute("doctorId");
        if (bsId == null) bsId = session.getAttribute("bacSiId");
        if (bsId != null) {
            try { doctorId = Integer.valueOf(bsId.toString()); }
            catch (NumberFormatException ignored) {}
        }
        if (doctorId == null) {
            Object bsObj = session.getAttribute("userLuuSinh");
            if (bsObj instanceof BacSi) doctorId = ((BacSi) bsObj).getId();
        }

        // Lấy danh sách lịch khám HOAN_THANH của bác sĩ từ DB
        java.util.List<com.phongkham.booking.entity.LichKham> records = new ArrayList<>();
        if (doctorId != null) {
            try {
                List<LichKham> ds = lichKhamService.getLichByBacSi(doctorId);
                if (ds != null) {
                    for (LichKham lk : ds) {
                        if (lk != null && "HOAN_THANH".equalsIgnoreCase(lk.getTrangThai())) {
                            records.add(lk);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Lỗi truy vấn hồ sơ bệnh án: " + e.getMessage());
            }
        }

        // Lọc theo từ khóa (tên người bệnh / SĐT)
        final String kw = (keyword != null ? keyword.trim().toLowerCase() : "");
        // Lọc theo ngày khám
        final String selectedDateStr = date;

// Gom nhóm theo bệnh nhân (ưu tiên ID bệnh nhân, fallback email/SĐT/tên) -> PatientSummary
        java.util.LinkedHashMap<String, PatientSummary> patientMap = new java.util.LinkedHashMap<>();
        java.util.List<LichKham> allRecordsForPatient = new ArrayList<>();

        for (LichKham lk : records) {
            String phone = lk.getSoDienThoai() != null ? lk.getSoDienThoai() : "";
            String name = lk.getHoTenBenhNhan() != null ? lk.getHoTenBenhNhan() : "Bệnh nhân";
            String email = lk.getEmail() != null ? lk.getEmail() : "";
            String key = buildPatientKey(lk);
            if (key.isBlank()) key = name;

            // Filter theo keyword
            if (!kw.isEmpty()) {
                boolean m = name.toLowerCase().contains(kw) || phone.toLowerCase().contains(kw) || email.toLowerCase().contains(kw);
                if (!m) continue;
            }
            // Filter theo ngày khám
            if (selectedDateStr != null && !selectedDateStr.isBlank()) {
                String ngay = lk.getNgayKham() != null ? lk.getNgayKham() : "";
                if (!ngay.equals(selectedDateStr)) continue;
            }

String[] gtTuoi = layGioiTinhTuoi(lk);
            patientMap.putIfAbsent(key, new PatientSummary(key, name, phone, email,
                    gtTuoi[0], gtTuoi[1], lk.getNgayKham()));
            allRecordsForPatient.add(lk);
        }

        java.util.List<PatientSummary> patients = new ArrayList<>(patientMap.values());
        model.addAttribute("patients", patients);

// Xử lý bệnh nhân được chọn: ưu tiên patientKey (định danh duy nhất),
        // fallback patientId (ID hash cũ) và cuối cùng là bệnh nhân đầu danh sách.
        LichKham selectedLK = null;
        String selectedKey = (patientKey != null && !patientKey.isBlank()) ? patientKey : null;
        if (selectedKey == null && patientId != null) {
            // patientId cũ = hash của key -> tìm lịch có key.hashCode() trùng
            for (LichKham lk : allRecordsForPatient) {
                String k = buildPatientKey(lk);
                if (k != null && ((long) (k.hashCode() & 0x7fffffff)) == patientId.longValue()) {
                    selectedLK = lk;
                    selectedKey = k;
                    break;
                }
            }
        } else if (selectedKey != null) {
            for (LichKham lk : allRecordsForPatient) {
                if (selectedKey.equals(buildPatientKey(lk))) {
                    selectedLK = lk;
                    break;
                }
            }
        }
        if (selectedLK == null && !allRecordsForPatient.isEmpty()) {
            selectedLK = allRecordsForPatient.get(0);
            selectedKey = buildPatientKey(selectedLK);
        }

if (selectedLK != null) {
            String[] gtTuoiSel = layGioiTinhTuoi(selectedLK);
            String selKey = (selectedKey != null) ? selectedKey : buildPatientKey(selectedLK);
            PatientSummary sel = new PatientSummary(
                    selKey,
                    selectedLK.getHoTenBenhNhan() != null ? selectedLK.getHoTenBenhNhan() : "Bệnh nhân",
                    selectedLK.getSoDienThoai() != null ? selectedLK.getSoDienThoai() : "",
                    selectedLK.getEmail() != null ? selectedLK.getEmail() : "",
                    gtTuoiSel[0], gtTuoiSel[1],
                    selectedLK.getNgayKham());
            model.addAttribute("selectedPatient", sel);
            model.addAttribute("selectedPatientId", sel.id);
            model.addAttribute("selectedPatientKey", selKey);

// Lịch sử khám của bệnh nhân được chọn (dùng key định danh bệnh nhân để khớp chính xác)
            java.util.List<MedicalRecordView> histories = new ArrayList<>();
            for (LichKham lk : allRecordsForPatient) {
                if (selKey != null && selKey.equals(buildPatientKey(lk))) {
                    histories.add(new MedicalRecordView(lk));
                }
            }
            model.addAttribute("medicalRecords", histories);
        } else {
            model.addAttribute("selectedPatient", null);
            model.addAttribute("medicalRecords", new ArrayList<>());
        }

        return "chi_tiet_ho_so"; 
    }

    // 3. Quản lý Nhắc nhở
    @GetMapping({"/bac_si/nhac_nho", "/bac_si/nhac-nho"})
    public String trangNhacNho(HttpSession session, Model model) {
        String doctorEmail = getDoctorEmailFromSession(session);
        if (doctorEmail == null) {
            return "redirect:/dang_nhap";
        }

        // Xác định ID bác sĩ từ session
        Integer doctorId = null;
        Object bsId = session.getAttribute("doctorId");
        if (bsId == null) bsId = session.getAttribute("bacSiId");
        if (bsId != null) {
            try { doctorId = Integer.valueOf(bsId.toString()); }
            catch (NumberFormatException ignored) {}
        }
        if (doctorId == null) {
            Object bsObj = session.getAttribute("userLuuSinh");
            if (bsObj instanceof BacSi) doctorId = ((BacSi) bsObj).getId();
        }

List<com.phongkham.booking.entity.LichKham> reminders = new ArrayList<>();
        try {
            List<com.phongkham.booking.entity.LichKham> ds = lichKhamService.getDanhSachTaiKham();
            if (ds != null) reminders = ds;
        } catch (Exception e) {
            System.err.println("Lỗi truy vấn nhắc nhở: " + e.getMessage());
        }
        model.addAttribute("reminders", reminders);

        return "nhac_nho"; 
    }

// ===== DTO nội bộ phục vụ trang Hồ sơ bệnh án =====
    public static class PatientSummary {
        public Long id;
        public String key;
        public String fullName;
        public String code;
        public String phone;
        public String gender;
        public String age;
        public String initials;
        public String lastVisitDate;

public PatientSummary(String key, String name, String phone, String email, String gender,
                              String age, String lastVisit) {
            this.key = key;
            this.id = (long) (key.hashCode() & 0x7fffffff);
            this.fullName = name;
            this.code = "BN" + this.id;
            this.phone = phone;
            this.gender = (gender == null || gender.isBlank()) ? "Chưa rõ" : gender;
            this.age = (age == null || age.isBlank()) ? "" : age;
            this.initials = name != null && !name.isBlank() ? name.substring(0, 1).toUpperCase() : "BN";
            this.lastVisitDate = lastVisit;
        }
    }

// Xây key định danh duy nhất cho bệnh nhân: ưu tiên ID bệnh nhân, fallback email/SĐT
    private String buildPatientKey(LichKham lk) {
        if (lk == null) return "";
        // 1. ID bệnh nhân (đã đăng nhập) — định danh chính xác nhất
        if (lk.getBenhNhan() != null && lk.getBenhNhan().getId() != null) {
            return "uid:" + lk.getBenhNhan().getId();
        }
        if (lk.getNguoiDungId() != null) {
            return "uid:" + lk.getNguoiDungId();
        }
        // 2. Email (unique trong bảng tài khoản)
        if (lk.getEmail() != null && !lk.getEmail().isBlank()) {
            return "email:" + lk.getEmail().trim().toLowerCase();
        }
        // 3. SĐT (fallback, có thể trùng — chấp nhận để không mất dữ liệu)
        if (lk.getSoDienThoai() != null && !lk.getSoDienThoai().isBlank()) {
            return "sdt:" + lk.getSoDienThoai().trim();
        }
        // 4. Cuối cùng dùng tên (kèm id lịch để tránh gộp sai)
        if (lk.getHoTenBenhNhan() != null && !lk.getHoTenBenhNhan().isBlank()) {
            return "ten:" + lk.getHoTenBenhNhan().trim() + "-" + lk.getId();
        }
        return "";
    }

    // Tìm giới tính + tuổi từ lịch khám: ưu tiên đối tượng benhNhan đã gắn trong lịch
    private String[] layGioiTinhTuoi(LichKham lk) {
        if (lk == null) return new String[]{"Chưa rõ", ""};
        // Ưu tiên bệnh nhân đã gắn trực tiếp trong lịch (chính xác 100%)
        if (lk.getBenhNhan() != null && lk.getBenhNhan().getId() != null) {
            Optional<NguoiDungBenhNhan> opt = khoNguoiDungBenhNhan.findById(lk.getBenhNhan().getId());
            if (opt.isPresent()) {
                return gioiTinhTuoiTuBenhNhan(opt.get());
            }
        }
        if (lk.getNguoiDungId() != null) {
            Optional<NguoiDungBenhNhan> opt = khoNguoiDungBenhNhan.findById(lk.getNguoiDungId());
            if (opt.isPresent()) {
                return gioiTinhTuoiTuBenhNhan(opt.get());
            }
        }
        // Fallback: tra qua email (unique) trước, SĐT sau
        if (lk.getEmail() != null && !lk.getEmail().isBlank()) {
            Optional<NguoiDungBenhNhan> opt = khoNguoiDungBenhNhan.findByEmail(lk.getEmail().trim());
            if (opt.isPresent()) {
                return gioiTinhTuoiTuBenhNhan(opt.get());
            }
        }
        if (lk.getSoDienThoai() != null && !lk.getSoDienThoai().isBlank()) {
            Optional<NguoiDungBenhNhan> opt = khoNguoiDungBenhNhan.findFirstBySoDienThoai(lk.getSoDienThoai().trim());
            if (opt.isPresent()) {
                return gioiTinhTuoiTuBenhNhan(opt.get());
            }
        }
        return new String[]{"Chưa rõ", ""};
    }

    // Trích giới tính + tuổi từ 1 đối tượng bệnh nhân
    private String[] gioiTinhTuoiTuBenhNhan(NguoiDungBenhNhan bn) {
        if (bn == null) return new String[]{"Chưa rõ", ""};
        String gioiTinh = bn.getGioiTinh() != null ? bn.getGioiTinh() : "Chưa rõ";
        String tuoi = bn.getTuoi() > 0 ? String.valueOf(bn.getTuoi()) : "";
        return new String[]{gioiTinh, tuoi};
    }

    // Tìm giới tính + tuổi của bệnh nhân từ tài khoản đăng ký (NguoiDungBenhNhan) theo SĐT/email
    private String[] layGioiTinhTuoi(String phone, String email) {
        NguoiDungBenhNhan bn = null;
if (phone != null && !phone.isBlank()) {
            bn = khoNguoiDungBenhNhan.findFirstBySoDienThoai(phone.trim()).orElse(null);
        }
        if (bn == null && email != null && !email.isBlank()) {
            bn = khoNguoiDungBenhNhan.findByEmail(email.trim()).orElse(null);
        }
        if (bn == null) return new String[]{"Chưa rõ", ""};
        String gioiTinh = bn.getGioiTinh() != null ? bn.getGioiTinh() : "Chưa rõ";
        String tuoi = bn.getTuoi() > 0 ? String.valueOf(bn.getTuoi()) : "";
        return new String[]{gioiTinh, tuoi};
    }

    public static class MedicalRecordView {
        public Long id;
        public String visitDate;
        public String reason;
        public String doctorName;
        public String diagnosis;

        public MedicalRecordView(LichKham lk) {
            this.id = lk.getId() != null ? lk.getId().longValue() : 0L;
            this.visitDate = lk.getNgayKham() != null ? lk.getNgayKham() : "";
            this.reason = lk.getGhiChu() != null ? lk.getGhiChu() : "";
            this.doctorName = lk.getTenBacSi() != null ? lk.getTenBacSi() : "Bác sĩ điều trị";
            this.diagnosis = lk.getChanDoan() != null ? lk.getChanDoan() : "";
        }
    }
}
