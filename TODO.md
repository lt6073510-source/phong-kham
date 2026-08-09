# TODO - KỊCH BẢN 1: Chuông thông báo động + lưu bảng ThongBao

## FILE MỚI
- [x] 1. entity/ThongBao.java
- [x] 2. repository/ThongBaoRepository.java
- [x] 3. service/ThongBaoService.java

## FILE SỬA
- [x] 4. LichKhamService.java (chèn tạo thông báo vào xacNhan/huy/hoanThanh + getDanhSachTaiKham)
- [x] 5. LichKhamRepository.java (thêm findByTrangThaiAndNgayTaiKhamIsNotNull)
- [x] 6. LichKham.java (thêm getSoNgayConLai)
- [x] 7. BacSiController.java (/bac_si/nhac-nho đổ reminders)
- [x] 8. nhac_nho.html (bảng # | Bệnh nhân | SĐT | Ngày tái khám | Còn lại)

## BƯỚC 2: HOÀN THIỆN CHUÔNG ĐỘNG + TỰ ĐỘNG TÁI KHÁM
- [x] 9. ThongBaoRepository.java (thêm existsByLichKhamIdAndLoai để tránh trùng)
- [x] 10. ThongBaoService.java (thêm taoThongBaoTaiKham + kiểm tra trùng)
- [x] 11. BookingApplication.java (bật @EnableScheduling)
- [x] 12. WebController.java (đổ notifications + notificationCount)
- [x] 13. TaiKhamNotificationScheduler.java (job tự động tạo TAI_KHAM khi còn 3 ngày)
- [x] 14. index.html (chuông 🔔 + CSS + JS toggle + hiện thông báo động)

## SAU KHI SỬA
- [x] Build & compile thành công (BUILD SUCCESS)
- [ ] Test (thủ công): bác sĩ xác nhận/hủy/hoàn thành → bệnh nhân thấy chuông
- [ ] Test (thủ công): /bac_si/nhac-nho hiển thị danh sách tái khám
- [ ] Test (thủ công): job tự động tạo TAI_KHAM khi còn 3 ngày (chạy 00:30 hằng ngày)

## GHI CHÚ BỔ SUNG (BUỔI LÀM VIỆC SAU)
- Đã sửa `BacSiController.trangHoSoBenhAn`: gom nhóm bệnh nhân theo ID (không theo SĐT) qua `buildPatientKey`,
  và lấy tuổi/giới tính từ đúng đối tượng bệnh nhân qua `layGioiTinhTuoi(LichKham)`.
  → Khắc phục: bệnh nhân trùng SĐT không bị gộp mất; tuổi/giới tính hiển thị đúng khi có ngày sinh.
- `lich_su_dang_ky.html` dùng navbar riêng (Bootstrap) — chưa có chuông; nếu cần đồng bộ chuông ở trang này thì bổ sung sau.

---

# TODO - KỊCH BẢN 2: Dashboard Admin - 2 biểu đồ (Doughnut + Bar)

## MỤC TIÊU
Thêm 2 biểu đồ trực quan vào tab "Trang Chủ" của Admin:
- 🥧 Doughnut chart: Trạng thái lịch khám (Chờ xác nhận / Đã xác nhận / Hoàn thành / Đã hủy) + text tổng ở giữa.
- 📊 Bar chart: Lịch khám theo chuyên khoa (dữ liệu động từ DB).

## FILE SỬA
- [ ] 1. `AdminViewController.java` — thêm vào `adminPage()`: tính `thongKeTrangThai` (từ `getThongKeTrangThai()`)
      và `thongKeChuyenKhoa` (Map tên chuyên khoa → số lịch, gom null vào "Chưa phân khoa") rồi đưa vào model.
- [ ] 2. `admin.html` — thêm Chart.js CDN + 2 card biểu đồ nằm ngang (grid 2 cột, chiều cao ~400px,
      responsive về 1 cột trên màn hình nhỏ) + script Chart.js nhận dữ liệu động từ model.

## SAU KHI SỬA
- [ ] Build & compile thành công (BUILD SUCCESS).
- [ ] Test (thủ công): đăng nhập Admin → tab Trang Chủ hiển thị 2 biểu đồ đúng dữ liệu động.
- [ ] Test (thủ công): trường hợp không có lịch khám → hiển thị "Chưa có dữ liệu".
