package com.liull19.interview.controller;

import com.liull19.interview.aop.RateLimit;
import com.liull19.interview.model.resume.dto.ResumeDetailDTO;
import com.liull19.interview.model.resume.dto.ResumeListItemDTO;
import com.liull19.interview.service.Resume.ResumeHistoryService;
import com.liull19.interview.service.Resume.ResumeUploadService;
import com.liull19.interview.utils.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Slf4j
@Tag(name = "简历管理", description = "简历上传、分析、导出与删除")
public class ResumeController {

    private final ResumeHistoryService historyService;
    private final ResumeUploadService uploadService;


    /**
     * 获取所有简历列表
     */
    @GetMapping("/api/resumes")
    public Result<List<ResumeListItemDTO>> getAllResumes() {
        List<ResumeListItemDTO> resumes = historyService.getAllResumes();
        return Result.success(resumes);
    }

    /**
     * 上传简历并获取分析结果
     *
     * @param file 简历文件（支持PDF、DOCX、DOC、TXT、MD等）
     * @return 简历分析结果，包含评分和建议
     */
    @PostMapping(value = "/api/resumes/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RateLimit(dimension = RateLimit.Dimension.GLOBAL, count = 5)
    @RateLimit(dimension = RateLimit.Dimension.IP, count = 5)
    public Result<Map<String, Object>> uploadAndAnalyze(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = uploadService.uploadAndAnalyze(file);
        boolean isDuplicate = (Boolean) result.get("duplicate");
        if (isDuplicate) {
            return Result.success("检测到相同简历，已返回历史分析结果", result);
        }
        return Result.success(result);
    }


    /**
     * 健康检查接口
     */
    @GetMapping("/api/resumes/health")
    public Result<Map<String, String>> health() {
        return Result.success(Map.of(
                "status", "UP",
                "service", "AI Interview Platform - Resume Service"
        ));
    }

    /**
     * 获取简历详情（包含分析历史）
     */
    @GetMapping("/api/resumes/{id}/detail")
    public Result<ResumeDetailDTO> getResumeDetail(@PathVariable Long id) {
        ResumeDetailDTO detail = historyService.getResumeDetail(id);
        return Result.success(detail);
    }
}
