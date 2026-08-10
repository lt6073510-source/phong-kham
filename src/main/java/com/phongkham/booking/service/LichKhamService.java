package com.phongkham.booking.service;

import com.phongkham.booking.entity.BacSi;
import com.phongkham.booking.entity.LichKham;
import com.phongkham.booking.repository.BacSiRepository;
import com.phongkham.booking.repository.LichKhamRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LichKhamService {

    private final LichKhamRepository lichKhamRepository;
    private final ThongBaoService thongBaoService;
    private final BacSiRepository bacSiRepository;

    public LichKhamService(
            LichKhamRepository lichKhamRepository,
            ThongBaoService thongBaoService,
            BacSiRepository bacSiRepository) {

        this.lichKhamRepository = lichKhamRepository;
        this.thongBaoService = thongBaoService;
        this.bacSiRepository = bacSiRepository;
    }

    // =========================================================
    // 1. LẤY TẤT CẢ / LỌC THEO TRẠNG THÁI
    // =========================================================

    public List<LichKham> getAllLichKham(String status) {
        if (status != null && !status.isBlank()) {
            return lichKhamRepository.findByTrangThai(
                    status.trim().toUpperCase()
            );
        }

        return lichKhamRepository.findAll();
    }

    // =========================================================
    // 2. LẤY CHI TIẾT LỊCH KHÁM
    // =========================================================

    public Optional<LichKham> getLichKhamById(Integer id) {
        return lichKhamRepository.findById(id);
    }

    // =========================================================
    // 3. LẤY LỊCH THEO BÁC SĨ
    // =========================================================

    public List<LichKham> getLichByBacSi(Integer bacSiId) {
        return lichKhamRepository.findByBacSi_Id(bacSiId);
    }

    // =========================================================
    // 4. LẤY LỊCH THEO BỆNH NHÂN
    // =========================================================

    public List<LichKham> getLichByBenhNhanId(Long benhNhanId) {
        return lichKhamRepository.findByBenhNhan_Id(benhNhanId);
    }

    // =========================================================
    // 5. LẤY LỊCH THEO EMAIL
    // =========================================================

    public List<LichKham> getLichByEmail(String email) {
        return lichKhamRepository.findByEmail(email);
    }

    // =========================================================
    // 6. LẤY LỊCH THEO SỐ ĐIỆN THOẠI
    // =========================================================

    public List<LichKham> getLichBySoDienThoai(String soDienThoai) {
        return lichKhamRepository.findBySoDienThoai(soDienThoai);
    }

    // =========================================================
    // 7. THỐNG KÊ THEO TRẠNG THÁI
    // =========================================================

    public Map<String, Long> getThongKeTrangThai() {

        Map<String, Long> stats = new HashMap<>();

        stats.put(
                "choXacNhan",
                lichKhamRepository.countByTrangThai("CHO_XAC_NHAN")
        );

        stats.put(
                "daXacNhan",
                lichKhamRepository.countByTrangThai("DA_XAC_NHAN")
        );

        stats.put(
                "hoanThanh",
                lichKhamRepository.countByTrangThai("HOAN_THANH")
        );

        stats.put(
                "daHuy",
                lichKhamRepository.countByTrangThai("DA_HUY")
        );

        return stats;
    }

    // =========================================================
    // 8. TẠO LỊCH KHÁM MỚI
    //    CÓ CHỐNG TRÙNG LỊCH
    // =========================================================

    public LichKham taoLichKham(LichKham lichKham) {

        if (lichKham == null) {
            throw new RuntimeException(
                    "Thông tin lịch khám không được để trống!"
            );
        }

        /*
         * Nếu chưa chọn bác sĩ thì cho phép tạo lịch.
         *
         * Trường hợp này phù hợp với form hiện tại của bạn
         * vì bác sĩ đang là "Không bắt buộc".
         *
         * Khi chưa có bác sĩ thì không thể kiểm tra
         * trùng lịch theo bác sĩ.
         */
        if (lichKham.getBacSi() != null
                && lichKham.getBacSi().getId() != null) {

            Integer bacSiId = lichKham.getBacSi().getId();

            if (lichKham.getNgayKham() == null
                    || lichKham.getNgayKham().isBlank()) {

                throw new RuntimeException(
                        "Vui lòng chọn ngày khám!"
                );
            }

            /*
             * Hiện tại form của bạn đang lưu:
             *
             * Sang
             * Chieu
             *
             * vào gioKham.
             *
             * Vì vậy chống trùng dựa trên:
             * Bác sĩ + Ngày + Buổi khám.
             */
            if (lichKham.getGioKham() == null
                    || lichKham.getGioKham().isBlank()) {

                throw new RuntimeException(
                        "Vui lòng chọn buổi khám!"
                );
            }

            boolean trungLich =
                    lichKhamRepository.existsLichTrung(
                            bacSiId,
                            lichKham.getNgayKham(),
                            lichKham.getGioKham()
                    );

            if (trungLich) {

                throw new RuntimeException(
                        "Bác sĩ đã có lịch khám vào ngày "
                                + lichKham.getNgayKham()
                                + " - "
                                + lichKham.getGioKham()
                                + ". Vui lòng chọn thời gian khác!"
                );
            }
        }

        return lichKhamRepository.save(lichKham);
    }

    // =========================================================
    // 9. SỬA LỊCH KHÁM
    //    CÓ CHỐNG TRÙNG LỊCH
    // =========================================================

    public LichKham suaLichKham(
            Integer id,
            Integer bacSiId,
            String ngayKham,
            String gioKham) {

        LichKham lichKham = lichKhamRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy lịch khám với ID: " + id
                        )
                );

        if (bacSiId == null) {
            throw new RuntimeException(
                    "Vui lòng chọn bác sĩ!"
            );
        }

        if (ngayKham == null || ngayKham.isBlank()) {
            throw new RuntimeException(
                    "Vui lòng chọn ngày khám!"
            );
        }

        if (gioKham == null || gioKham.isBlank()) {
            throw new RuntimeException(
                    "Vui lòng chọn buổi khám!"
            );
        }

        /*
         * Kiểm tra trùng nhưng loại trừ chính lịch
         * đang được sửa.
         */
        boolean trungLich =
                lichKhamRepository.existsLichTrungKhiSua(
                        id,
                        bacSiId,
                        ngayKham,
                        gioKham
                );

        if (trungLich) {

            throw new RuntimeException(
                    "Bác sĩ đã có lịch khám vào ngày "
                            + ngayKham
                            + " - "
                            + gioKham
                            + ". Vui lòng chọn thời gian khác!"
            );
        }

        /*
         * Lấy bác sĩ thật từ database.
         */
        BacSi bacSi = bacSiRepository.findById(bacSiId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy bác sĩ với ID: "
                                        + bacSiId
                        )
                );

        /*
         * Cập nhật lịch.
         */
        lichKham.setBacSi(bacSi);
        lichKham.setTenBacSi(bacSi.getHoTen());

        lichKham.setNgayKham(ngayKham);
        lichKham.setGioKham(gioKham);

        return lichKhamRepository.save(lichKham);
    }

    // =========================================================
    // 10. BÁC SĨ XÁC NHẬN LỊCH
    // =========================================================

    public LichKham xacNhanLichKham(Integer id) {

        LichKham lichKham = lichKhamRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy lịch khám với ID: " + id
                        )
                );

        lichKham.setTrangThai("DA_XAC_NHAN");

        LichKham saved =
                lichKhamRepository.save(lichKham);

        Long ndId =
                getNguoiDungIdCuaLich(saved);

        if (ndId != null) {

            thongBaoService.taoThongBao(
                    ndId,
                    "Lịch khám của bạn vào ngày "
                            + safeNgay(saved.getNgayKham())
                            + " đã được xác nhận.",
                    "XAC_NHAN",
                    saved.getId()
            );
        }

        return saved;
    }

    // =========================================================
    // 11. BÁC SĨ HỦY LỊCH
    // =========================================================

    public LichKham huyLichKham(
            Integer id,
            String lyDoHuy) {

        LichKham lichKham =
                lichKhamRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy lịch khám với ID: "
                                                + id
                                )
                        );

        lichKham.setTrangThai("DA_HUY");

        if (lyDoHuy != null
                && !lyDoHuy.isBlank()) {

            lichKham.setLyDoHuy(
                    lyDoHuy.trim()
            );
        }

        LichKham saved =
                lichKhamRepository.save(lichKham);

        Long ndId =
                getNguoiDungIdCuaLich(saved);

        if (ndId != null) {

            String lyDo =
                    (lyDoHuy != null
                            && !lyDoHuy.isBlank())
                            ? lyDoHuy
                            : "không có lý do";

            thongBaoService.taoThongBao(
                    ndId,
                    "Lịch khám của bạn vào ngày "
                            + safeNgay(saved.getNgayKham())
                            + " đã bị hủy. Lý do: "
                            + lyDo,
                    "HUY",
                    saved.getId()
            );
        }

        return saved;
    }

    // =========================================================
    // 12. BÁC SĨ HOÀN THÀNH KHÁM
    // =========================================================

    public LichKham hoanThanhKham(
            Integer id,
            String chanDoan,
            String donThuoc,
            String ngayTaiKhamStr) {

        LichKham lichKham =
                lichKhamRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy lịch khám với ID: "
                                                + id
                                )
                        );

        lichKham.setTrangThai("HOAN_THANH");

        lichKham.setChanDoan(chanDoan);

        lichKham.setDonThuoc(donThuoc);

        if (ngayTaiKhamStr != null
                && !ngayTaiKhamStr.isBlank()) {

            lichKham.setNgayTaiKham(
                    ngayTaiKhamStr.trim()
            );

        } else {

            lichKham.setNgayTaiKham(null);
        }

        LichKham saved =
                lichKhamRepository.save(lichKham);

        Long ndId =
                getNguoiDungIdCuaLich(saved);

        if (ndId != null) {

            String noiDung =
                    "Lịch khám của bạn đã hoàn thành.";

            if (saved.getNgayTaiKham() != null
                    && !saved.getNgayTaiKham().isBlank()) {

                noiDung =
                        "Lịch khám của bạn đã hoàn thành. "
                                + "Bạn có lịch tái khám vào ngày "
                                + saved.getNgayTaiKham()
                                + ".";
            }

            thongBaoService.taoThongBao(
                    ndId,
                    noiDung,
                    "HOAN_THANH",
                    saved.getId()
            );
        }

        return saved;
    }

    // =========================================================
    // 13. XÓA LỊCH KHÁM
    // =========================================================

    public void xoaLichKham(Integer id) {
        lichKhamRepository.deleteById(id);
    }

    // =========================================================
    // 14. DANH SÁCH NHẮC NHỞ TÁI KHÁM CỦA BÁC SĨ
    // =========================================================

    public List<LichKham> getDanhSachNhacNho(
            Integer bacSiId) {

        if (bacSiId == null) {
            return List.of();
        }

        return lichKhamRepository
                .findByBacSi_IdAndNgayTaiKhamIsNotNullAndNgayTaiKhamNot(
                        bacSiId,
                        ""
                );
    }

    // =========================================================
    // 15. THỐNG KÊ HÔM NAY CỦA BÁC SĨ
    // =========================================================

    public Map<String, Long> getThongKeTrangThaiByBacSi(
            Integer bacSiId) {

        Map<String, Long> stats =
                new HashMap<>();

        String homNay =
                LocalDate.now().toString();

        if (bacSiId == null) {

            stats.put("choXacNhan", 0L);
            stats.put("daXacNhan", 0L);
            stats.put("hoanThanh", 0L);
            stats.put("daHuy", 0L);

            return stats;
        }

        stats.put(
                "choXacNhan",
                lichKhamRepository
                        .countByBacSi_IdAndNgayKhamAndTrangThai(
                                bacSiId,
                                homNay,
                                "CHO_XAC_NHAN"
                        )
        );

        stats.put(
                "daXacNhan",
                lichKhamRepository
                        .countByBacSi_IdAndNgayKhamAndTrangThai(
                                bacSiId,
                                homNay,
                                "DA_XAC_NHAN"
                        )
        );

        stats.put(
                "hoanThanh",
                lichKhamRepository
                        .countByBacSi_IdAndNgayKhamAndTrangThai(
                                bacSiId,
                                homNay,
                                "HOAN_THANH"
                        )
        );

        stats.put(
                "daHuy",
                lichKhamRepository
                        .countByBacSi_IdAndNgayKhamAndTrangThai(
                                bacSiId,
                                homNay,
                                "DA_HUY"
                        )
        );

        return stats;
    }

    // =========================================================
    // 16. LỊCH KHÁM HÔM NAY CỦA BÁC SĨ
    // =========================================================

    public List<LichKham> getLichHomNayByBacSi(
            Integer bacSiId) {

        if (bacSiId == null) {
            return List.of();
        }

        String homNay =
                LocalDate.now().toString();

        return lichKhamRepository
                .findByBacSi_IdAndNgayKhamOrderByIdDesc(
                        bacSiId,
                        homNay
                );
    }

    // =========================================================
    // 17. DANH SÁCH LỊCH TÁI KHÁM
    // =========================================================

    public List<LichKham> getDanhSachTaiKham() {

        return lichKhamRepository
                .findByTrangThaiAndNgayTaiKhamIsNotNull(
                        "HOAN_THANH"
                );
    }

    // =========================================================
    // 18. TÁI KHÁM SẮP ĐẾN
    // =========================================================

    public List<LichKham> getDanhSachTaiKhamSapDen(
            int maxNgay) {

        List<LichKham> all =
                getDanhSachTaiKham();

        if (all == null || all.isEmpty()) {
            return List.of();
        }

        List<LichKham> result =
                new ArrayList<>();

        for (LichKham lich : all) {

            long soNgay =
                    lich.getSoNgayConLai();

            /*
             * 0  = hôm nay
             * 1  = ngày mai
             * ...
             *
             * Không lấy lịch đã quá hạn.
             */
            if (soNgay >= 0
                    && soNgay <= maxNgay) {

                result.add(lich);
            }
        }

        return result;
    }

    // =========================================================
    // 19. LẤY ID NGƯỜI DÙNG NHẬN THÔNG BÁO
    // =========================================================

    private Long getNguoiDungIdCuaLich(
            LichKham lich) {

        if (lich == null) {
            return null;
        }

        if (lich.getBenhNhan() != null
                && lich.getBenhNhan().getId() != null) {

            return lich.getBenhNhan().getId();
        }

        if (lich.getNguoiDungId() != null) {
            return lich.getNguoiDungId();
        }

        return null;
    }

    // =========================================================
    // 20. HÀM PHỤ - NGÀY AN TOÀN
    // =========================================================

    private String safeNgay(String ngay) {

        return (ngay == null || ngay.isBlank())
                ? "đã đặt"
                : ngay;
    }
}