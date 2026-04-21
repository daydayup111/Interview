package com.liull19.interview.service.Resume;

import com.liull19.interview.constant.ErrorCode;
import com.liull19.interview.exception.BusinessException;
import com.liull19.interview.mapper.resume.InterviewMapper;
import com.liull19.interview.mapper.resume.ResumeMapper;
import com.liull19.interview.model.resume.dto.ResumeDetailDTO;
import com.liull19.interview.model.resume.dto.ResumeListItemDTO;
import com.liull19.interview.model.resume.entity.ResumeAnalysisEntity;
import com.liull19.interview.model.resume.entity.ResumeEntity;
import com.liull19.interview.service.impl.Resume.InterviewPersistenceService;
import com.liull19.interview.service.impl.Resume.ResumePersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

/**
 * 简历历史服务 简历历史和导出简历分析报告
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ResumeHistoryService {

    private final ResumePersistenceService resumePersistenceService;
    private final InterviewPersistenceService interviewPersistenceService;
    private final ResumeMapper resumeMapper;
    private final InterviewMapper interviewMapper;
    private final ObjectMapper objectMapper;


    /**
     * 获取所有简历列表
     */
    public List<ResumeListItemDTO> getAllResumes() {
        List<ResumeEntity> resumes = resumePersistenceService.findAllResumes();

        return resumes.stream().map(resume -> {
            // 获取最新分析结果的分数
            Integer latestScore = null;
            java.time.LocalDateTime lastAnalyzedAt = null;
            Optional<ResumeAnalysisEntity> analysisOpt = resumePersistenceService.getLatestAnalysis(resume.getId());
            if (analysisOpt.isPresent()) {
                ResumeAnalysisEntity analysis = analysisOpt.get();
                latestScore = analysis.getOverallScore();
                lastAnalyzedAt = analysis.getAnalyzedAt();
            }

            // 获取面试次数
            int interviewCount = interviewPersistenceService.findByResumeId(resume.getId()).size();

            // 使用 MapStruct 映射
            return new ResumeListItemDTO(
                    resume.getId(),
                    resume.getOriginalFilename(),
                    resume.getFileSize(),
                    resume.getUploadedAt(),
                    resume.getAccessCount(),
                    latestScore,
                    lastAnalyzedAt,
                    interviewCount,
                    resume.getAnalyzeStatus(),
                    resume.getAnalyzeError()
            );
        }).toList();
    }

    /**
     * 获取简历详情（包含分析历史）
     */
    public ResumeDetailDTO getResumeDetail(Long id) {
        Optional<ResumeEntity> resumeOpt = resumePersistenceService.findById(id);
        if (resumeOpt.isEmpty()) {
            throw new BusinessException(ErrorCode.RESUME_NOT_FOUND);
        }

        ResumeEntity resume = resumeOpt.get();

        // 获取所有分析记录，使用 MapStruct 批量转换
        List<ResumeAnalysisEntity> analyses = resumePersistenceService.findAnalysesByResumeId(id);
        List<ResumeDetailDTO.AnalysisHistoryDTO> analysisHistory = resumeMapper.toAnalysisHistoryDTOList(
                analyses,
                this::extractStrengths,
                this::extractSuggestions
        );

        // 使用 InterviewMapper 转换面试历史
        List<Object> interviewHistory = interviewMapper.toInterviewHistoryList(
                interviewPersistenceService.findByResumeId(id)
        );

        return new ResumeDetailDTO(
                resume.getId(),
                resume.getOriginalFilename(),
                resume.getFileSize(),
                resume.getContentType(),
                resume.getStorageUrl(),
                resume.getUploadedAt(),
                resume.getAccessCount(),
                resume.getResumeText(),
                resume.getAnalyzeStatus(),
                resume.getAnalyzeError(),
                analysisHistory,
                interviewHistory
        );
    }

    /**
     * 从 JSON 提取 strengths
     */
    private List<String> extractStrengths(ResumeAnalysisEntity entity) {
        try {
            if (entity.getStrengthsJson() != null) {
                return objectMapper.readValue(
                        entity.getStrengthsJson(),
                        new TypeReference<>() {
                        }
                );
            }
        } catch (JacksonException e) {
            log.error("解析 strengths JSON 失败", e);
        }
        return List.of();
    }

    /**
     * 从 JSON 提取 suggestions
     */
    private List<Object> extractSuggestions(ResumeAnalysisEntity entity) {
        try {
            if (entity.getSuggestionsJson() != null) {
                return objectMapper.readValue(
                        entity.getSuggestionsJson(),
                        new TypeReference<>() {
                        }
                );
            }
        } catch (JacksonException e) {
            log.error("解析 suggestions JSON 失败", e);
        }
        return List.of();
    }
}
