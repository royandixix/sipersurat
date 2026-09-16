package com.sipersurat.backend.controller;
import com.sipersurat.backend.dto.archive.ArchiveResponse;
import com.sipersurat.backend.response.ApiResponse;
import com.sipersurat.backend.service.ArchiveService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/v1/archives")
public class ArchiveController{
    private final ArchiveService service;
    public ArchiveController(ArchiveService service){this.service=service;}
    @GetMapping public ApiResponse<List<ArchiveResponse>> getAll(){return ApiResponse.ok("Data arsip berhasil dimuat",service.getAll());}
    @PatchMapping("/{source}/{id}/restore") public ApiResponse<ArchiveResponse> restore(@PathVariable String source,@PathVariable Long id){return ApiResponse.ok("Arsip berhasil dipulihkan",service.restore(source,id));}
    @DeleteMapping("/{source}/{id}") public ApiResponse<Void> delete(@PathVariable String source,@PathVariable Long id){service.deletePermanent(source,id);return ApiResponse.ok("Arsip berhasil dihapus permanen",null);}
}
