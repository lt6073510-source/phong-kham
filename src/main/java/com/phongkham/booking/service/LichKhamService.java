package com.phongkham.booking.service;

import com.phongkham.booking.entity.LichKham;
import com.phongkham.booking.repository.LichKhamRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LichKhamService {

    private final LichKhamRepository lichKhamRepository;

    public LichKhamService(LichKhamRepository lichKhamRepository) {
        this.lichKhamRepository = lichKhamRepository;
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
    public List<LichKham> getLichByBenhNhanId(Integer benhNhanId) {
        return lichKhamRepository.findByBenhNhanId(benhNhanId);
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
        return lichKhamRepository.save(lichKham);
    }

    // 10. Bác sĩ Hủy lịch khám
    public LichKham huyLichKham(Integer id, String lyDoHuy) {
        LichKham lichKham = lichKhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch khám với ID: " + id));
        lichKham.setTrangThai("DA_HUY");
        if (lyDoHuy != null && !lyDoHuy.isBlank()) {
            lichKham.setLyDoHuy(lyDoHuy);
        }
        return lichKhamRepository.save(lichKham);
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
        
        return lichKhamRepository.save(lichKham);
    }

    // 12. Xóa lịch khám
    public void xoaLichKham(Integer id) {
        lichKhamRepository.deleteById(id);
    }
    // Thống kê theo trạng thái dành riêng cho từng Bác sĩ
public Map<String, Long> getThongKeTrangThaiByBacSi(Integer bacSiId) {
    Map<String, Long> stats = new HashMap<>();
    stats.put("choXacNhan", lichKhamRepository.countByBacSi_IdAndTrangThai(bacSiId, "CHO_XAC_NHAN"));
    stats.put("daXacNhan", lichKhamRepository.countByBacSi_IdAndTrangThai(bacSiId, "DA_XAC_NHAN"));
    stats.put("hoanThanh", lichKhamRepository.countByBacSi_IdAndTrangThai(bacSiId, "HOAN_THANH"));
    stats.put("daHuy", lichKhamRepository.countByBacSi_IdAndTrangThai(bacSiId, "DA_HUY"));
    return stats;
}
}