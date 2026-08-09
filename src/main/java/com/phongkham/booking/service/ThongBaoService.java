package com.phongkham.booking.service;

import com.phongkham.booking.entity.ThongBao;
import com.phongkham.booking.repository.ThongBaoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ThongBaoService {

    private final ThongBaoRepository thongBaoRepository;

    public ThongBaoService(ThongBaoRepository thongBaoRepository) {
        this.thongBaoRepository = thongBaoRepository;
    }

    // Tạo một thông báo mới cho người dùng (bệnh nhân)
    public ThongBao taoThongBao(Long nguoiDungId, String noiDung, String loai, Integer lichKhamId) {
        if (nguoiDungId == null) return null;

        ThongBao tb = new ThongBao();
        tb.setNguoiDungId(nguoiDungId);
        tb.setNoiDung(noiDung);
        tb.setLoai(loai);
        tb.setLichKhamId(lichKhamId);
        tb.setDaDoc(false);
        tb.setNgayTao(LocalDateTime.now());
        return thongBaoRepository.save(tb);
    }

    // Lấy danh sách thông báo của người dùng (mới nhất trước)
    public List<ThongBao> getThongBaoByNguoiDung(Long nguoiDungId) {
        if (nguoiDungId == null) return List.of();
        return thongBaoRepository.findByNguoiDungIdOrderByNgayTaoDesc(nguoiDungId);
    }

    // Đếm số thông báo chưa đọc của người dùng
    public long countChuaDoc(Long nguoiDungId) {
        if (nguoiDungId == null) return 0;
        return thongBaoRepository.countByNguoiDungIdAndDaDocFalse(nguoiDungId);
    }

    // Đánh dấu tất cả thông báo của người dùng là đã đọc
    public void danhDauDaDoc(Long nguoiDungId) {
        if (nguoiDungId == null) return;
        List<ThongBao> list = thongBaoRepository.findByNguoiDungIdOrderByNgayTaoDesc(nguoiDungId);
        for (ThongBao tb : list) {
            if (!Boolean.TRUE.equals(tb.getDaDoc())) {
                tb.setDaDoc(true);
                thongBaoRepository.save(tb);
            }
        }
    }

    // Tạo thông báo tái khám (TAI_KHAM) cho bệnh nhân nếu CHƯA tồn tại thông báo cùng loại cho lịch này.
    // Hàm chạy trong job tự động -> tránh tạo lặp mỗi lần chạy.
    public ThongBao taoThongBaoTaiKham(Long nguoiDungId, Integer lichKhamId, String ngayTaiKham, long soNgayConLai) {
        if (nguoiDungId == null || lichKhamId == null) return null;
        // Nếu đã có thông báo TAI_KHAM cho lịch này rồi thì bỏ qua (chống trùng)
        boolean exists = thongBaoRepository.existsByNguoiDungIdAndLichKhamIdAndLoai(nguoiDungId, lichKhamId, "TAI_KHAM");
        if (exists) return null;

        String ngayDisplay = (ngayTaiKham == null || ngayTaiKham.isBlank()) ? "đã đặt" : ngayTaiKham;
        String phanNgay;
        if (soNgayConLai == 0) {
            phanNgay = " hôm nay.";
        } else if (soNgayConLai == 1) {
            phanNgay = ". Còn 1 ngày nữa.";
        } else {
            phanNgay = ". Còn " + soNgayConLai + " ngày nữa.";
        }

        String noiDung = "Bạn có lịch tái khám vào ngày " + ngayDisplay + phanNgay
                + " Vui lòng sắp xếp thời gian đến phòng khám.";

        return taoThongBao(nguoiDungId, noiDung, "TAI_KHAM", lichKhamId);
    }

    // Kiểm tra đã tồn tại thông báo cùng loại cho lịch khám chưa (phục vụ chống trùng lặp)
    public boolean daCoThongBao(Long nguoiDungId, Integer lichKhamId, String loai) {
        if (nguoiDungId == null || lichKhamId == null) return false;
        return thongBaoRepository.existsByNguoiDungIdAndLichKhamIdAndLoai(nguoiDungId, lichKhamId, loai);
    }
}
