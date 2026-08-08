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
- [ ] 9. index.html (chuông 🔔 + CSS + JS toggle + hiện thông báo động)
- [ ] 10. WebController.java (đổ notifications + notificationCount)

## SAU KHI SỬA
- [ ] Build & chạy thử
- [ ] Test: bác sĩ xác nhận/hủy/hoàn thành → bệnh nhân thấy chuông
- [ ] Test: /bac_si/nhac-nho hiển thị danh sách tái khám
