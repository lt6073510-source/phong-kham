package com.phongkham.booking.repository;

import com.phongkham.booking.entity.LichKham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichKhamRepository extends JpaRepository<LichKham, Integer> {

    // 1. Tìm lịch khám theo trạng thái
    List<LichKham> findByTrangThai(String trangThai);

    // 2. Tìm lịch khám theo Bác sĩ ID (Truy vấn qua đối tượng bacSi.id)
    List<LichKham> findByBacSi_Id(Integer bacSiId);

    // 3. Lọc lịch khám theo Bác sĩ ID và Trạng thái
    List<LichKham> findByBacSi_IdAndTrangThai(Integer bacSiId, String trangThai);

    // 4. Tìm lịch khám theo Bệnh nhân ID
    List<LichKham> findByBenhNhanId(Integer benhNhanId);

    // 5. Tìm lịch khám theo Email
    List<LichKham> findByEmail(String email);

    // 6. Tìm lịch khám theo SĐT
    List<LichKham> findBySoDienThoai(String soDienThoai);

    // 7. Đếm số lượng theo Trạng thái của riêng Bác sĩ
    long countByBacSi_IdAndTrangThai(Integer bacSiId, String trangThai);

    // 8. Đếm số lượng theo Trạng thái chung
    long countByTrangThai(String trangThai);

    // 9. Lấy danh sách lịch tái khám của Bác sĩ (Kiểm tra khác null và không rỗng)
    List<LichKham> findByBacSi_IdAndNgayTaiKhamIsNotNullAndNgayTaiKhamNot(Integer bacSiId, String emptyStr);
}