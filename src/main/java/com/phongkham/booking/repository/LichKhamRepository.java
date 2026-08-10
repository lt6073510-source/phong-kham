package com.phongkham.booking.repository;

import com.phongkham.booking.entity.LichKham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichKhamRepository extends JpaRepository<LichKham, Integer> {

    // =========================================================
    // 1. Tìm lịch khám theo trạng thái
    // =========================================================
    List<LichKham> findByTrangThai(String trangThai);


    // =========================================================
    // 2. Tìm lịch khám theo Bác sĩ ID
    // =========================================================
    List<LichKham> findByBacSi_Id(Integer bacSiId);


    // =========================================================
    // 3. Lọc lịch khám theo Bác sĩ ID và Trạng thái
    // =========================================================
    List<LichKham> findByBacSi_IdAndTrangThai(
            Integer bacSiId,
            String trangThai
    );


    // =========================================================
    // 4. Tìm lịch khám theo Bệnh nhân ID
    // =========================================================
    List<LichKham> findByBenhNhan_Id(Long benhNhanId);


    // =========================================================
    // 5. Tìm lịch khám theo Email
    // =========================================================
    List<LichKham> findByEmail(String email);


    // =========================================================
    // 6. Tìm lịch khám theo Số điện thoại
    // =========================================================
    List<LichKham> findBySoDienThoai(String soDienThoai);


    // =========================================================
    // 7. Đếm số lượng theo Bác sĩ + Trạng thái
    // =========================================================
    long countByBacSi_IdAndTrangThai(
            Integer bacSiId,
            String trangThai
    );


    // =========================================================
    // 8. Đếm số lượng theo Trạng thái chung
    // =========================================================
    long countByTrangThai(String trangThai);


    // =========================================================
    // 9. Lấy danh sách lịch tái khám của Bác sĩ
    // Có ngày tái khám và ngày tái khám không rỗng
    // =========================================================
    List<LichKham> findByBacSi_IdAndNgayTaiKhamIsNotNullAndNgayTaiKhamNot(
            Integer bacSiId,
            String emptyStr
    );


    // =========================================================
    // 10. Đếm lịch theo Bác sĩ + Ngày khám + Trạng thái
    // Dùng cho "Tổng quan hôm nay"
    // =========================================================
    long countByBacSi_IdAndNgayKhamAndTrangThai(
            Integer bacSiId,
            String ngayKham,
            String trangThai
    );


    // =========================================================
    // 11. Lấy lịch của Bác sĩ theo ngày khám
    // Sắp xếp ID giảm dần
    // =========================================================
    List<LichKham> findByBacSi_IdAndNgayKhamOrderByIdDesc(
            Integer bacSiId,
            String ngayKham
    );


    // =========================================================
    // 12. Lấy lịch tái khám theo trạng thái
    // Có ngày tái khám
    // =========================================================
    List<LichKham> findByTrangThaiAndNgayTaiKhamIsNotNull(
            String trangThai
    );


    // =========================================================
    // 13. KIỂM TRA TRÙNG LỊCH KHI TẠO LỊCH MỚI
    //
    // Trùng khi:
    // - Cùng bác sĩ
    // - Cùng ngày khám
    // - Cùng buổi/giờ khám
    //
    // Lịch đã hủy sẽ KHÔNG được tính là trùng.
    // =========================================================
    @Query("""
        SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END
        FROM LichKham l
        WHERE l.bacSi.id = :bacSiId
          AND l.ngayKham = :ngayKham
          AND l.gioKham = :gioKham
          AND l.trangThai <> 'DA_HUY'
    """)
    boolean existsLichTrung(
            @Param("bacSiId") Integer bacSiId,
            @Param("ngayKham") String ngayKham,
            @Param("gioKham") String gioKham
    );


    // =========================================================
    // 14. KIỂM TRA TRÙNG LỊCH KHI SỬA LỊCH
    //
    // Giống kiểm tra trùng ở trên nhưng:
    // - Bỏ qua chính lịch đang được sửa
    // - l.id <> :id
    //
    // Ví dụ:
    // Lịch ID 10 đang sửa từ:
    // 10/08 - Sáng
    //
    // thành:
    // 10/08 - Sáng
    //
    // => Không báo trùng với chính nó.
    //
    // Nhưng nếu đã có lịch ID 15 cùng:
    // - Bác sĩ
    // - Ngày
    // - Buổi
    //
    // => Báo trùng.
    // =========================================================
    @Query("""
        SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END
        FROM LichKham l
        WHERE l.bacSi.id = :bacSiId
          AND l.ngayKham = :ngayKham
          AND l.gioKham = :gioKham
          AND l.id <> :id
          AND l.trangThai <> 'DA_HUY'
    """)
    boolean existsLichTrungKhiSua(
            @Param("id") Integer id,
            @Param("bacSiId") Integer bacSiId,
            @Param("ngayKham") String ngayKham,
            @Param("gioKham") String gioKham
    );
}