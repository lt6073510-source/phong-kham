package com.phongkham.booking.service;

import com.phongkham.booking.entity.LichKham;
import com.phongkham.booking.repository.LichKhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BenhNhanService {

    @Autowired
    private LichKhamRepository lichKhamRepository;

    // ==========================================
    // 1. LUỒNG BỆNH NHÂN (PATIENT)
    // ==========================================

    /**
     * Bệnh nhân đăng ký đặt lịch khám (Kiểm tra trùng giờ bác sĩ trước khi lưu)
     */
    public boolean saveBenhNhan(LichKham lichKham) {
        // Kiểm tra xem đã tồn tại lịch trùng của Bác sĩ + Ngày khám + Giờ khám này chưa
        List<LichKham> existingLich = lichKhamRepository.findAll();
        boolean isTrung = existingLich.stream()
            .anyMatch(lk -> lk.getBacSiId().equals(lichKham.getBacSiId()) &&
                           lk.getNgayKham().equals(lichKham.getNgayKham()) &&
                           lk.getGioKham().equals(lichKham.getGioKham()));

        if (isTrung) {
            return false; // Đã có người đặt -> Trả về false để Controller báo lỗi
        }

        // Chưa trùng thì set trạng thái mặc định và lưu vào CSDL
        if (lichKham.getTrangThai() == null) {
            lichKham.setTrangThai("CHO_XAC_NHAN");
        }

        lichKhamRepository.save(lichKham);
        return true;
    }

    /**
     * Bệnh nhân xem lịch sử các lần đặt khám theo Email
     */
    public List<LichKham> getLichSuKhamByEmail(String email) {
        return lichKhamRepository.findByEmail(email);
    }

    /**
     * Bệnh nhân chủ động hủy lịch đặt
     */
    public boolean benhNhanHuyLich(Integer id, String lyDo) {
        Optional<LichKham> opt = lichKhamRepository.findById(id);
        if (opt.isPresent()) {
            LichKham lk = opt.get();
            lk.setTrangThai("DA_HUY");
            lk.setLyDoHuy(lyDo);
            lichKhamRepository.save(lk);
            return true;
        }
        return false;
    }

    // ==========================================
    // 2. LUỒNG QUẢN TRỊ VIÊN (ADMIN)
    // ==========================================

    /**
     * Admin xem toàn bộ danh sách lịch hẹn khám của tất cả bệnh nhân
     */
    public List<LichKham> getAllLichKham() {
        return lichKhamRepository.findAll();
    }

    /**
     * Admin duyệt lịch / chuyển trạng thái (CHO_XAC_NHAN -> DA_XAC_NHAN -> DA_KHAM / DA_HUY)
     */
    public boolean updateTrangThaiLich(Integer id, String trangThaiMoi) {
        Optional<LichKham> opt = lichKhamRepository.findById(id);
        if (opt.isPresent()) {
            LichKham lk = opt.get();
            lk.setTrangThai(trangThaiMoi);
            lichKhamRepository.save(lk);
            return true;
        }
        return false;
    }

    /**
     * Admin xóa lịch hẹn khỏi hệ thống
     */
    public boolean xoaLichKham(Integer id) {
        if (lichKhamRepository.existsById(id)) {
            lichKhamRepository.deleteById(id);
            return true;
        }
        return false;
    }
}