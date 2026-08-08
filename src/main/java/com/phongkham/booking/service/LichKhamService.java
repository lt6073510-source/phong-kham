package com.phongkham.booking.service;

import com.phongkham.booking.entity.LichKham;
import com.phongkham.booking.repository.LichKhamRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LichKhamService {

    private final LichKhamRepository lichKhamRepository;
    private final ThongBaoService thongBaoService;

    public LichKhamService(LichKhamRepository lichKhamRepository, ThongBaoService thongBaoService) {
        this.lichKhamRepository = lichKhamRepository;
        this.thongBaoService = thongBaoService;
    }

    // 1. Lấy tất cả hoặc lọc theo trạng thái
    public List<LichKham> getAllLichKham(String status) {
        if (status != null && !status.isBlank()) {
            return lichKhamRepository.findByTrangThai(status.trim().toUpperCase());
        }
        return lichKhamRepository.findAll();
    }

    // 2. Lấy chi tiết lịch khám theo ID
    public Optional<LichKham> getLichKhamById(Integer id) {
        return lichKhamRepository.findById(id);
    }

    // 3. Lấy lịch khám theo Bác sĩ ID (Kiểm tra đúng tên hàm trong Repository)
    public List<LichKham> getLichByBacSi(Integer bacSiId) {
        return lichKhamRepository.findByBacSi_Id(bacSiId); 
    }

    // 4. Lấy lịch khám theo Bệnh nhân ID
    public List<LichKham> getLichByBenhNhanId(Long benhNhanId) {
        return lichKhamRepository.findByBenhNhan_Id(benhNhanId);
    }

    // 5. Lấy lịch khám theo Email
    public List<LichKham> getLichByEmail(String email) {
        return lichKhamRepository.findByEmail(email);
    }

    // 6. Lấy lịch khám theo Số điện thoại
    public List<LichKham> getLichBySoDienThoai(String soDienThoai) {
        return lichKhamRepository.findBySoDienThoai(soDienThoai);
    }

    // 7. Thống kê số lượng theo các trạng thái
    public Map<String, Long> getThongKeTrangThai() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("choXacNhan", lichKhamRepository.countByTrangThai("CHO_XAC_NHAN"));
        stats.put("daXacNhan", lichKhamRepository.countByTrangThai("DA_XAC_NHAN"));
        stats.put("hoanThanh", lichKhamRepository.countByTrangThai("HOAN_THANH"));
        stats.put("daHuy", lichKhamRepository.countByTrangThai("DA_HUY"));
        return stats;
    }

    // 8. Tạo lịch khám mới
    public LichKham taoLichKham(LichKham lichKham) {
        return lichKhamRepository.save(lichKham);
    }

    // 9. Bác sĩ Xác nhận lịch
    public LichKham xacNhanLichKham(Integer id) {
        LichKham lichKham = lichKhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch khám với ID: " + id));
        lichKham.setTrangThai("DA_XAC_NHAN");
        LichKham saved = lichKhamRepository.save(lichKham);

        Long ndId = getNguoiDungIdCuaLich(saved);
        if (ndId != null) {
            thongBaoService.taoThongBao(ndId,
                    "Lịch khám của bạn vào ngày " + safeNgay(saved.getNgayKham()) + " đã được xác nhận.",
                    "XAC_NHAN", saved.getId());
        }
        return saved;
    }

    // 10. Bác sĩ Hủy lịch khám
    public LichKham huyLichKham(Integer id, String lyDoHuy) {
        LichKham lichKham = lichKhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch khám với ID: " + id));
        lichKham.setTrangThai("DA_HUY");
        if (lyDoHuy != null && !lyDoHuy.isBlank()) {
            lichKham.setLyDoHuy(lyDoHuy);
        }
        LichKham saved = lichKhamRepository.save(lichKham);

        Long ndId = getNguoiDungIdCuaLich(saved);
        if (ndId != null) {
            String lyDo = (lyDoHuy != null && !lyDoHuy.isBlank()) ? lyDoHuy : "không có lý do";
            thongBaoService.taoThongBao(ndId,
                    "Lịch khám của bạn vào ngày " + safeNgay(saved.getNgayKham()) + " đã bị hủy. Lý do: " + lyDo,
                    "HUY", saved.getId());
        }
        return saved;
    }

    // 11. Bác sĩ Hoàn thành khám (Đã sửa trangThai thành HOAN_THANH)
    public LichKham hoanThanhKham(Integer id, String chanDoan, String donThuoc, String ngayTaiKhamStr) {
        LichKham lichKham = lichKhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch khám với ID: " + id));
        
        lichKham.setTrangThai("HOAN_THANH"); // Sửa DA_KHAM -> HOAN_THANH cho khớp với JS/Controller
        lichKham.setChanDoan(chanDoan);
        lichKham.setDonThuoc(donThuoc);
        
        if (ngayTaiKhamStr != null && !ngayTaiKhamStr.isBlank()) {
            lichKham.setNgayTaiKham(ngayTaiKhamStr.trim());
        } else {
            lichKham.setNgayTaiKham(null);
        }
        
        LichKham saved = lichKhamRepository.save(lichKham);

        Long ndId = getNguoiDungIdCuaLich(saved);
        if (ndId != null) {
            String noiDung = "Lịch khám của bạn đã hoàn thành.";
            if (saved.getNgayTaiKham() != null && !saved.getNgayTaiKham().isBlank()) {
                noiDung = "Lịch khám của bạn đã hoàn thành. Bạn có lịch tái khám vào ngày " + saved.getNgayTaiKham() + ".";
            }
            thongBaoService.taoThongBao(ndId, noiDung, "HOAN_THANH", saved.getId());
        }
        return saved;
    }

// 12. Xóa lịch khám
    public void xoaLichKham(Integer id) {
        lichKhamRepository.deleteById(id);
    }

    // 13. Lấy danh sách nhắc nhở tái khám của Bác sĩ (lịch có ngayTaiKham)
    public List<LichKham> getDanhSachNhacNho(Integer bacSiId) {
        if (bacSiId == null) return List.of();
        return lichKhamRepository.findByBacSi_IdAndNgayTaiKhamIsNotNullAndNgayTaiKhamNot(bacSiId, "");
    }
// Thống kê theo trạng thái & ngày hôm nay dành riêng cho từng Bác sĩ (dành cho "Tổng quan hôm nay")
    public Map<String, Long> getThongKeTrangThaiByBacSi(Integer bacSiId) {
        Map<String, Long> stats = new HashMap<>();
        String homNay = java.time.LocalDate.now().toString();
        if (bacSiId == null) {
            stats.put("choXacNhan", 0L);
            stats.put("daXacNhan", 0L);
            stats.put("hoanThanh", 0L);
            stats.put("daHuy", 0L);
            return stats;
        }
        stats.put("choXacNhan", lichKhamRepository.countByBacSi_IdAndNgayKhamAndTrangThai(bacSiId, homNay, "CHO_XAC_NHAN"));
        stats.put("daXacNhan", lichKhamRepository.countByBacSi_IdAndNgayKhamAndTrangThai(bacSiId, homNay, "DA_XAC_NHAN"));
        stats.put("hoanThanh", lichKhamRepository.countByBacSi_IdAndNgayKhamAndTrangThai(bacSiId, homNay, "HOAN_THANH"));
        stats.put("daHuy", lichKhamRepository.countByBacSi_IdAndNgayKhamAndTrangThai(bacSiId, homNay, "DA_HUY"));
        return stats;
    }

// Lấy danh sách lịch khám của Bác sĩ theo đúng ngày hôm nay (sắp giảm theo ID)
    public List<LichKham> getLichHomNayByBacSi(Integer bacSiId) {
        if (bacSiId == null) return List.of();
        String homNay = java.time.LocalDate.now().toString();
        return lichKhamRepository.findByBacSi_IdAndNgayKhamOrderByIdDesc(bacSiId, homNay);
    }

    // Lấy danh sách lịch tái khám (trangThai = HOAN_THANH và có ngayTaiKham)
    public List<LichKham> getDanhSachTaiKham() {
        return lichKhamRepository.findByTrangThaiAndNgayTaiKhamIsNotNull("HOAN_THANH");
    }

    // Lấy danh sách lịch tái khám còn lại trong vòng <= maxNgay (dùng cho chuông thông báo bệnh nhân)
    public List<LichKham> getDanhSachTaiKhamSapDen(int maxNgay) {
        List<LichKham> all = getDanhSachTaiKham();
        if (all == null || all.isEmpty()) return List.of();
        List<LichKham> result = new ArrayList<>();
        for (LichKham l : all) {
            long soNgay = l.getSoNgayConLai();
            // Còn trong khoảng 0..maxNgay (bao gồm hôm nay), bỏ qua đã quá lịch
            if (soNgay >= 0 && soNgay <= maxNgay) {
                result.add(l);
            }
        }
        return result;
    }

    // Lấy ID người dùng (bệnh nhân) nhận thông báo từ lịch khám
    private Long getNguoiDungIdCuaLich(LichKham lich) {
        if (lich == null) return null;
        if (lich.getBenhNhan() != null && lich.getBenhNhan().getId() != null) {
            return lich.getBenhNhan().getId();
        }
        if (lich.getNguoiDungId() != null) {
            return lich.getNguoiDungId();
        }
        return null;
    }

    // Hàm phụ: trả về chuỗi an toàn cho ngày
    private String safeNgay(String ngay) {
        return (ngay == null || ngay.isBlank()) ? "đã đặt" : ngay;
    }
}
