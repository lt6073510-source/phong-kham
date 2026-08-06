package com.phongkham.booking.service;

import com.phongkham.booking.entity.ChuyenKhoa;
import com.phongkham.booking.repository.ChuyenKhoaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChuyenKhoaService {

    private final ChuyenKhoaRepository chuyenKhoaRepository;

    public ChuyenKhoaService(ChuyenKhoaRepository chuyenKhoaRepository) {
        this.chuyenKhoaRepository = chuyenKhoaRepository;
    }

    // 1. Lấy tất cả chuyên khoa (GIỮ NGUYÊN)
    public List<ChuyenKhoa> getAllChuyenKhoa() {
        return chuyenKhoaRepository.findAll();
    }

    // 2. Lấy chi tiết chuyên khoa theo ID (GIỮ NGUYÊN)
    public Optional<ChuyenKhoa> getChuyenKhoaById(Integer id) {
        return chuyenKhoaRepository.findById(id);
    }

    // 3. Hàm THÊM / CẬP NHẬT chuyên khoa (GIỮ NGUYÊN)
    public ChuyenKhoa taoChuyenKhoa(ChuyenKhoa chuyenKhoa) {
        return chuyenKhoaRepository.save(chuyenKhoa);
    }

    // 4. Hàm XÓA chuyên khoa (GIỮ NGUYÊN)
    public void xoaChuyenKhoa(Integer id) {
        chuyenKhoaRepository.deleteById(id);
    }

    // 5. ĐÃ SỬA: Thay findByMa_khoa thành findByMaKhoa
    public ChuyenKhoa getChuyenKhoaByMa(String maKhoa) {
        return chuyenKhoaRepository.findByMaKhoa(maKhoa).orElse(null);
    }
}