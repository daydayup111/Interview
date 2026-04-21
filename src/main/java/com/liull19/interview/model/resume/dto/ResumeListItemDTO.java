package com.liull19.interview.model.resume.dto;

import com.liull19.interview.constant.AsyncTaskStatus;

import java.time.LocalDateTime;

/**
 * 简历列表DTO
 */

public record ResumeListItemDTO(
        Long id,
        String filename,
        Long fileSize,
        LocalDateTime uploadedAt,
        Integer accessCount,
        Integer latestScore,
        LocalDateTime lastAnalyzedAt,
        Integer interviewCount,
        AsyncTaskStatus analyzeStatus,
        String analyzeError
) {}
