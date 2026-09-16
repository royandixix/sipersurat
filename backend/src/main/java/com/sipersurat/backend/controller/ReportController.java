package com.sipersurat.backend.controller;
import com.sipersurat.backend.dto.report.ReportRecordResponse;
import com.sipersurat.backend.response.ApiResponse;
import com.sipersurat.backend.service.ReportService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/v1/reports")
public class ReportController{
    private final ReportService service;
    public ReportController(ReportService service){this.service=service;}
    @GetMapping public ApiResponse<List<ReportRecordResponse>> getAll(){return ApiResponse.ok("Laporan berhasil dimuat",service.getAll());}
}
