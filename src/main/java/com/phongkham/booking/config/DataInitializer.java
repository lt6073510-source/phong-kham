package com.phongkham.booking.config;

import com.phongkham.booking.entity.BacSi;
import com.phongkham.booking.entity.ChuyenKhoa;
import com.phongkham.booking.entity.HangMuc;
import com.phongkham.booking.repository.BacSiRepository;
import com.phongkham.booking.repository.ChuyenKhoaRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataInitializer {

    // Helper lưu cấu trúc thông tin bác sĩ ban đầu
    static class BacSiData {
        String hocVi;
        String hoTen;
        String keywordKhoa;
        int soNamKinhNghiem;
        BigDecimal giaKham;
        String hinhAnh;

        public BacSiData(String hocVi, String hoTen, String keywordKhoa, int soNamKinhNghiem, double giaKham, String hinhAnh) {
            this.hocVi = hocVi;
            this.hoTen = hoTen;
            this.keywordKhoa = keywordKhoa;
            this.soNamKinhNghiem = soNamKinhNghiem;
            this.giaKham = BigDecimal.valueOf(giaKham);
            this.hinhAnh = hinhAnh;
        }
    }

    @Bean
    @Transactional
    public CommandLineRunner initDatabase(
            ChuyenKhoaRepository chuyenKhoaRepo,
            BacSiRepository bacSiRepo) {
            
        return args -> {
            long countKhoa = chuyenKhoaRepo.count();
            long countBacSi = bacSiRepo.count();

            System.out.println(">>> CHECK DB CŨ: Chuyên khoa = " + countKhoa + " | Bác sĩ = " + countBacSi);

        // ==========================================
            // 1. KHỞI TẠO BẢNG CHUYÊN KHOA (NẾU TRỐNG)
            // ==========================================
            if (countKhoa == 0) {
                List<ChuyenKhoa> listKhoa = new ArrayList<>();

                // --- 1. KHOA NỘI ---
                ChuyenKhoa ck1 = new ChuyenKhoa();
                ck1.setMa_khoa("khoa-noi");
                ck1.setTenChuyenKhoa("Khoa Nội");
                ck1.setTen_chuyen_khoa("Khoa Nội Tổng Hợp");
                ck1.setHinhAnh("/images/anh_bia.jpeg"); // Gán ảnh bìa chung
                ck1.setMoTa("Chẩn đoán và điều trị chuyên sâu các bệnh lý cơ quan nội tạng.");
                ck1.setPhu_de_banner("Trung tâm tiếp nhận, chẩn đoán và điều trị nội khoa toàn diện tại Phòng khám.");
                ck1.setDoan_gioi_thieu("<strong>Khoa Nội</strong> là chuyên khoa lâm sàng chuyên tiếp nhận, chẩn đoán và điều trị toàn diện các bệnh lý xảy ra ở các cơ quan bên trong cơ thể bằng phương pháp sử dụng thuốc, kết hợp điều chỉnh chế độ dinh dưỡng và lối sống mà hoàn toàn không can thiệp bằng phẫu thuật.");
                ck1.setTieu_de_hang_muc("Các hệ cơ quan thăm khám chuyên sâu");
                ck1.setLoi_ket("Khoa Nội đóng vai trò là <strong>\"chìa khóa\"</strong> giúp phát hiện sớm các bất thường bên trong cơ thể, đưa ra phác đồ điều trị nội khoa chuẩn xác.");
                ck1.setDanh_sach_hang_muc(List.of(
                    new HangMuc("❤️", "Hệ Tim mạch", "Tầm soát và điều trị tăng huyết áp, rối loạn nhịp tim, thiếu máu cơ tim."),
                    new HangMuc("🫁", "Hệ Hô hấp", "Khám các bệnh lý viêm đường hô hấp cấp, viêm phế quản mạn tính, hen suyễn."),
                    new HangMuc("🍽️", "Hệ Tiêu hóa", "Điều trị đau dạ dày, trào ngược, viêm đại tràng, các bệnh lý về gan và mật."),
                    new HangMuc("🧪", "Hệ Nội tiết", "Quản lý và điều trị bệnh lý đái tháo đường, rối loạn tuyến giáp."),
                    new HangMuc("🧠", "Hệ Thần kinh", "Khám và cải thiện chứng đau đầu, mất ngủ, chóng mặt, suy giảm trí nhớ."),
                    new HangMuc("🦴", "Hệ Cơ xương khớp", "Điều trị viêm khớp, thoái hóa khớp, bệnh gout và các cơn đau khớp mạn tính.")
                ));
                listKhoa.add(ck1);

                // --- 2. KHOA NGOẠI ---
                ChuyenKhoa ck2 = new ChuyenKhoa();
                ck2.setMa_khoa("khoa-ngoai");
                ck2.setTenChuyenKhoa("Khoa Ngoại");
                ck2.setTen_chuyen_khoa("Khoa Ngoại Tổng Hợp");
                ck2.setHinhAnh("/images/anh_bia.jpeg"); // Gán ảnh bìa chung
                ck2.setMoTa("Tư vấn, chẩn đoán và thực hiện các phẫu thuật từ tiểu phẫu đến đại phẫu.");
                ck2.setPhu_de_banner("Trung tâm chẩn đoán, điều trị và phẫu thuật ngoại khoa an toàn.");
                ck2.setDoan_gioi_thieu("<strong>Khoa Ngoại</strong> là chuyên khoa lâm sàng chuyên chẩn đoán, điều trị và sửa chữa các tổn thương, dị tật hoặc bệnh lý của các cơ quan trong cơ thể bằng phương pháp phẫu thuật hoặc thủ thuật can thiệp trực tiếp.");
                ck2.setTieu_de_hang_muc("Các lĩnh vực điều trị và can thiệp ngoại khoa");
                ck2.setLoi_ket("Khoa Ngoại đóng vai trò quyết định trong việc giải quyết nhanh chóng và triệt để các bệnh lý cấp cứu hoặc mạn tính mà phương pháp dùng thuốc không thể chữa khỏi.");
                ck2.setDanh_sach_hang_muc(List.of(
                    new HangMuc("🩺", "Ngoại Tiêu hóa", "Phẫu thuật cắt ruột thừa, sỏi mật, thoát vị bẹn, viêm loét đường tiêu hóa."),
                    new HangMuc("🦴", "Chấn thương chỉnh hình", "Nắn chỉnh, phẫu thuật gãy xương, trật khớp, đứt dây chằng."),
                    new HangMuc("💧", "Ngoại Tiết niệu", "Tán sỏi hoặc phẫu thuật lấy sỏi thận, sỏi bàng quang, u xơ tuyến tiền liệt."),
                    new HangMuc("🩹", "Xử lý tiểu phẫu", "Khâu vết thương hở, cắt bỏ u bao hoạt dịch, u mỡ, mụn nhọt, xử lý áp xe.")
                ));
                listKhoa.add(ck2);

                // --- 3. KHOA MẮT ---
                ChuyenKhoa ck3 = new ChuyenKhoa();
                ck3.setMa_khoa("khoa-mat");
                ck3.setTenChuyenKhoa("Khoa Mắt");
                ck3.setTen_chuyen_khoa("Khoa Mắt Chuyên Sâu");
                ck3.setHinhAnh("/images/anh_bia.jpeg"); // Gán ảnh bìa chung
                ck3.setMoTa("Chăm sóc toàn diện thị lực, đo khúc xạ và điều trị các bệnh về mắt.");
                ck3.setPhu_de_banner("Trung tâm chăm sóc, chẩn đoán và điều trị toàn diện các bệnh lý về mắt.");
                ck3.setDoan_gioi_thieu("<strong>Khoa Mắt</strong> chuyên thăm khám, chẩn đoán, điều trị và chăm sóc toàn diện cho các bệnh lý, tật khúc xạ cùng các tổn thương liên quan đến mắt và thị giác.");
                ck3.setTieu_de_hang_muc("Các hạng mục thăm khám và điều trị");
                ck3.setLoi_ket("Khoa Mắt đóng vai trò quan trọng trong việc bảo vệ <strong>\"đôi mắt sáng\"</strong>, giúp phát hiện sớm các nguy cơ gây mù lòa và cải thiện chất lượng thị giác.");
                ck3.setDanh_sach_hang_muc(List.of(
                    new HangMuc("👓", "Tật khúc xạ", "Khám, đo thị lực và tư vấn điều trị cận thị, viễn thị, loạn thị, lão thị."),
                    new HangMuc("👁️", "Bệnh lý nhãn cầu", "Điều trị viêm kết mạc (đau mắt đỏ), viêm giác mạc, khô mắt, chắp, lẹo."),
                    new HangMuc("🔍", "Đục thủy tinh thể", "Thăm khám, quản lý và phẫu thuật thay thủy tinh thể (phương pháp Phaco)."),
                    new HangMuc("⚡", "Bệnh lý đáy mắt", "Tầm soát, điều trị bệnh võng mạc đái tháo đường, thoái hóa hoàng điểm.")
                ));
                listKhoa.add(ck3);

                // --- 4. KHOA DA LIỄU ---
                ChuyenKhoa ck4 = new ChuyenKhoa();
                ck4.setMa_khoa("khoa-da-lieu");
                ck4.setTenChuyenKhoa("Khoa Da Liễu");
                ck4.setTen_chuyen_khoa("Khoa Da Liễu & Thẩm Mỹ Da");
                ck4.setHinhAnh("/images/anh_bia.jpeg"); // Gán ảnh bìa chung
                ck4.setMoTa("Điều trị các bệnh lý về da, tóc, móng và tư vấn chăm sóc da chuyên sâu.");
                ck4.setPhu_de_banner("Trung tâm chăm sóc, điều trị chuyên sâu và phục hồi sức khỏe làn da.");
                ck4.setDoan_gioi_thieu("<strong>Khoa Da liễu</strong> chuyên thăm khám, chẩn đoán, điều trị và chăm sóc toàn diện cho các bệnh lý về da, lông, tóc, móng và các bệnh lây truyền qua đường tình dục.");
                ck4.setTieu_de_hang_muc("Các hạng mục điều trị và thẩm mỹ da");
                ck4.setLoi_ket("Khoa Da liễu đóng vai trò cốt lõi trong việc phục hồi hàng rào bảo vệ da, giải quyết triệt để các cơn ngứa ngáy, viêm nhiễm và mang lại sự tự tin.");
                ck4.setDanh_sach_hang_muc(List.of(
                    new HangMuc("🌿", "Bệnh da viêm & dị ứng", "Điều trị viêm da cơ địa, chàm (eczema), mề đay, vảy nến, tổ đỉa."),
                    new HangMuc("💧", "Bệnh da nhiễm trùng", "Điều trị mụn trứng cá, viêm nang lông, zona thần kinh, nấm da, hắc lào."),
                    new HangMuc("💇‍♀️", "Bệnh về tóc & móng", "Khám và điều trị rụng tóc, hói đầu, nấm móng, móng chọc thịt."),
                    new HangMuc("✨", "Thủ thuật & thẩm mỹ", "Đốt laser hoặc áp lạnh trị sẹo, mụn cóc, nốt ruồi, tàn nhang.")
                ));
                listKhoa.add(ck4);

                // --- 5. KHOA RĂNG HÀM MẶT ---
                ChuyenKhoa ck5 = new ChuyenKhoa();
                ck5.setMa_khoa("rang-ham-mat");
                ck5.setTenChuyenKhoa("Khoa Răng-Hàm-Mặt");
                ck5.setTen_chuyen_khoa("Khoa Răng - Hàm - Mặt");
                ck5.setHinhAnh("/images/anh_bia.jpeg"); // Gán ảnh bìa chung
                ck5.setMoTa("Dịch vụ nha khoa thẩm mỹ, chăm sóc răng miệng và phẫu thuật hàm mặt.");
                ck5.setPhu_de_banner("Trung tâm chăm sóc nụ cười, điều trị và phục hình thẩm mỹ nha khoa cao cấp.");
                ck5.setDoan_gioi_thieu("<strong>Khoa Răng - Hàm - Mặt</strong> chuyên thăm khám, chẩn đoán, điều trị và phục hình các bệnh lý, dị tật hoặc tổn thương liên quan đến răng, xương hàm và khoang miệng.");
                ck5.setTieu_de_hang_muc("Các hạng mục điều trị và thẩm mỹ nha khoa");
                ck5.setLoi_ket("Khoa Răng - Hàm - Mặt đóng vai trò quan trọng trong việc khôi phục chức năng ăn nhai, bảo vệ sức khỏe răng miệng toàn diện và kiến tạo nụ cười tự tin.");
                ck5.setDanh_sach_hang_muc(List.of(
                    new HangMuc("🦷", "Bệnh lý răng miệng", "Điều trị sâu răng, viêm tủy, viêm nướu, viêm quanh răng, hôi miệng."),
                    new HangMuc("🛠️", "Phẫu thuật miệng", "Nhổ răng khôn mọc lệch, mọc ngầm, phẫu thuật cắt nang chân răng."),
                    new HangMuc("✨", "Phục hình & thẩm mỹ", "Trồng răng Implant, làm răng giả tháo lắp, bọc răng sứ, tẩy trắng răng."),
                    new HangMuc("😁", "Chỉnh nha (Niềng răng)", "Khắc phục các tình trạng răng hô, móm, thưa, lệch lạc bằng mắc cài hoặc khay trong suốt.")
                ));
                listKhoa.add(ck5);

                chuyenKhoaRepo.saveAllAndFlush(listKhoa);
                System.out.println(">>> ĐÃ LƯU THÀNH CÔNG " + listKhoa.size() + " CHUYÊN KHOA VÀO CSDL!");
            }

            // ==========================================
            // 2. KHỞI TẠO DANH SÁCH BÁC SĨ (NẾU TRỐNG)
            // ==========================================
            if (countBacSi == 0) {
                List<ChuyenKhoa> dsKhoa = chuyenKhoaRepo.findAll();

                if (dsKhoa.isEmpty()) {
                    System.out.println(">>> LỖI: DANH SÁCH CHUYÊN KHOA RỐNG!");
                    return;
                }

                List<BacSiData> ds = new ArrayList<>();

                // === 1. KHOA NỘI (5 Bác sĩ) ===
                ds.add(new BacSiData("TS.BS", "Nguyễn Văn Minh", "Khoa Nội", 15, 300000, "/bs1.jpg"));
                ds.add(new BacSiData("ThS.BS", "Lê Thị Lan", "Khoa Nội", 10, 250000, "/bs4.jpg"));
                ds.add(new BacSiData("BS.CKII", "Trần Thanh Tùng", "Khoa Nội", 12, 280000, "/bs3.jpg"));
                ds.add(new BacSiData("BS.CKI", "Phạm Thị Mai", "Khoa Nội", 8, 200000, "/bs5.jpg"));
                ds.add(new BacSiData("BS", "Hoàng Văn Hải", "Khoa Nội", 5, 150000, "/bs2.jpg"));

                // === 2. KHOA NGOẠI (5 Bác sĩ) ===
                ds.add(new BacSiData("BS.CKII", "Trần Quốc Dũng", "Khoa Ngoại", 16, 350000, "/bs6.jpg"));
                ds.add(new BacSiData("ThS.BS", "Phạm Anh Tú", "Khoa Ngoại", 9, 250000, "/bs7.jpg"));
                ds.add(new BacSiData("BS.CKI", "Ngô Văn Đức", "Khoa Ngoại", 7, 200000, "/bs8.jpg"));
                ds.add(new BacSiData("BS.CKI", "Lê Hoàng Nam", "Khoa Ngoại", 8, 200000, "/bs9.jpg"));
                ds.add(new BacSiData("BS", "Vũ Đình Khoa", "Khoa Ngoại", 4, 150000, "/bs10.jpg"));

                // === 3. KHOA MẮT (3 Bác sĩ) ===
                ds.add(new BacSiData("BS.CKI", "Hoàng Mai Hoa", "Khoa Mắt", 9, 220000, "/bs13.jpg"));
                ds.add(new BacSiData("ThS.BS", "Đỗ Minh Khang", "Khoa Mắt", 11, 260000, "/bs12.jpg"));
                ds.add(new BacSiData("BS", "Nguyễn Phương Thảo", "Khoa Mắt", 5, 150000, "/bs11.jpg"));

                // === 4. KHOA DA LIỄU (3 Bác sĩ) ===
                ds.add(new BacSiData("TS.BS", "Trần Thu Hà", "Khoa Da Liễu", 14, 320000, "/bs18.jpg"));
                ds.add(new BacSiData("BS.CKII", "Nguyễn Văn An", "Khoa Da Liễu", 13, 300000, "/bs17.jpg"));
                ds.add(new BacSiData("ThS.BS", "Lê Hoàng Anh", "Khoa Da Liễu", 8, 220000, "/bs19.jpg"));

                // === 5. KHOA RĂNG HÀM MẶT (3 Bác sĩ) ===
                ds.add(new BacSiData("ThS.BS", "Vũ Hoàng Nam", "Khoa Răng-Hàm-Mặt", 10, 250000, "/bs14.jpg"));
                ds.add(new BacSiData("BS.CKI", "Trịnh Bảo Ngọc", "Khoa Răng-Hàm-Mặt", 7, 200000, "/bs15.jpg"));
                ds.add(new BacSiData("BS", "Bùi Anh Tuấn", "Khoa Răng-Hàm-Mặt", 4, 150000, "/bs16.jpg"));

                long startId = 12345678L;
                List<BacSi> listBacSi = new ArrayList<>();

                for (int i = 0; i < ds.size(); i++) {
                    BacSiData data = ds.get(i);
                    String id8Digits = String.valueOf(startId + i);

                    ChuyenKhoa ck = dsKhoa.stream()
                            .filter(k -> k.getTenChuyenKhoa() != null && 
                                         (k.getTenChuyenKhoa().equalsIgnoreCase(data.keywordKhoa) || 
                                          k.getTenChuyenKhoa().toLowerCase().contains(data.keywordKhoa.toLowerCase())))
                            .findFirst()
                            .orElse(dsKhoa.get(0));

                    BacSi bacSi = new BacSi();
                    bacSi.setHoTen(data.hoTen);
                    bacSi.setEmail("bs" + id8Digits + "@phongkham.com");
                    bacSi.setMatKhau(id8Digits);
                    bacSi.setSoDienThoai("09" + String.format("%08d", i + 1));
                    bacSi.setVaiTro("BAC_SI");
                    bacSi.setTrangThai("HOAT_DONG");
                    bacSi.setHocVi(data.hocVi);
                    bacSi.setChuyenKhoa(ck);
                    bacSi.setSoNamKinhNghiem(data.soNamKinhNghiem);
                    bacSi.setGiaKham(data.giaKham);
                    bacSi.setMoTa("Bác sĩ " + data.hocVi + " " + data.hoTen + " có " + data.soNamKinhNghiem + " năm kinh nghiệm làm việc tại " + ck.getTenChuyenKhoa());
                    bacSi.setAnh(data.hinhAnh);

                    listBacSi.add(bacSi);
                }

                bacSiRepo.saveAllAndFlush(listBacSi);
                System.out.println(">>> ĐÃ LƯU THÀNH CÔNG " + listBacSi.size() + " BÁC SĨ VÀO CSDL!");
            }
        };
    }
}