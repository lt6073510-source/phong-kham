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
}
