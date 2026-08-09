package com.phongkham.booking.config;

import com.phongkham.booking.entity.LichKham;
import com.phongkham.booking.service.LichKhamService;
import com.phongkham.booking.service.ThongBaoService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Job tự động tạo thông báo nhắc lịch tái khám (TAI_KHAM) cho bệnh nhân
 * khi còn <= 3 ngày nữa đến ngày tái khám.
 */
@Component
public class TaiKhamNotificationScheduler {

    private final LichKhamService lichKhamService;
    private final ThongBaoService thongBaoService;

    public TaiKhamNotificationScheduler(LichKhamService lichKhamService, ThongBaoService thongBaoService) {
        this.lichKhamService = lichKhamService;
        this.thongBaoService = thongBaoService;
    }

    // Chạy mỗi ngày lúc 00:30 (giờ đầu của ngày) để tạo thông báo cho các lịch sắp đến.
    @Scheduled(cron = "0 30 0 * * *")
    public void taoThongBaoTaiKhamTuDong() {
        try {
            // Chỉ nhắc khi còn <= 3 ngày
            List<LichKham> danhSach = lichKhamService.getDanhSachTaiKhamSapDen(3);
            if (danhSach == null || danhSach.isEmpty()) {
                return;
            }
            int dem = 0;
            for (LichKham lich : danhSach) {
                Long ndId = getNguoiDungIdCuaLich(lich);
                if (ndId == null || lich.getId() == null) continue;

                long soNgay = lich.getSoNgayConLai();
                // Chỉ tạo nếu còn trong khoảng 0..3 ngày (đã được lọc nhưng kiểm tra lại cho an toàn)
                if (soNgay < 0 || soNgay > 3) continue;

                var tb = thongBaoService.taoThongBaoTaiKham(ndId, lich.getId(), lich.getNgayTaiKham(), soNgay);
                if (tb != null) {
                    dem++;
                }
            }
            if (dem > 0) {
                System.out.println("[TaiKhamScheduler] Đã tạo " + dem + " thông báo nhắc lịch tái khám.");
            }
        } catch (Exception e) {
            System.err.println("[TaiKhamScheduler] Lỗi khi chạy job tạo thông báo tái khám: " + e.getMessage());
        }
    }

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
}
