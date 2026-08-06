package com.phongkham.booking.controller;

import com.phongkham.booking.entity.ChuyenKhoa;
import com.phongkham.booking.service.ChuyenKhoaService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping
public class ChuyenKhoaController {

    private final ChuyenKhoaService chuyenKhoaService;

    public ChuyenKhoaController(ChuyenKhoaService chuyenKhoaService) {
        this.chuyenKhoaService = chuyenKhoaService;
    }

    // ==========================================
    // CÁC HÀM CŨ CỦA BẠN (GIỮ NGUYÊN CẤU TRÚC VÀ ĐƯỜNG DẪN)
    // ==========================================

    @GetMapping({"/api/chuyenkhoa", "/api/chuyenkhoa/chuyen_khoa"})
    @ResponseBody
    public List<ChuyenKhoa> getAllChuyenKhoa() {
        return chuyenKhoaService.getAllChuyenKhoa();
    }

    @PostMapping("/api/chuyenkhoa")
    @ResponseBody
    public ChuyenKhoa taoChuyenKhoa(@RequestBody ChuyenKhoa chuyenKhoa) {
        return chuyenKhoaService.taoChuyenKhoa(chuyenKhoa);
    }

    // ==========================================
    // BỔ SUNG THÊM
    // ==========================================

    // 1. Trả về giao diện HTML khi truy cập URL (Ví dụ: /chuyen-khoa/khoa-noi)
    @GetMapping("/chuyen-khoa/{ma_khoa}")
    public String viewChuyenKhoaDetail(@PathVariable("ma_khoa") String maKhoa, Model model) {
        ChuyenKhoa chuyenKhoa = chuyenKhoaService.getChuyenKhoaByMa(maKhoa);
        
        // SỬA ĐÚNG CHỖ NÀY: Thay vì redirect làm sập trang, gán object rỗng để Thymeleaf không bị hỏng
        if (chuyenKhoa == null) {
            chuyenKhoa = new ChuyenKhoa();
            chuyenKhoa.setTen_chuyen_khoa("Chuyên khoa không tồn tại");
            chuyenKhoa.setPhu_de_banner("Không tìm thấy dữ liệu cho mã: " + maKhoa);
            chuyenKhoa.setDoan_gioi_thieu("Vui lòng kiểm tra lại đường dẫn URL.");
        }
        
        model.addAttribute("chuyenKhoa", chuyenKhoa);
        return "chuyen_khoa"; // Mở file templates/chuyen_khoa.html
    }

    // 2. API lấy thông tin chi tiết 1 khoa dạng JSON
    @GetMapping("/api/chuyenkhoa/{ma_khoa}")
    @ResponseBody
    public ChuyenKhoa getChuyenKhoaByMa(@PathVariable("ma_khoa") String maKhoa) {
        return chuyenKhoaService.getChuyenKhoaByMa(maKhoa);
    }
}