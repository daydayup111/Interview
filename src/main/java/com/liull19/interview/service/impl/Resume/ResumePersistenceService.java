package com.liull19.interview.service.impl.Resume;

import com.liull19.interview.constant.ErrorCode;
import com.liull19.interview.exception.BusinessException;
import com.liull19.interview.mapper.resume.ResumeAnalysisRepository;
import com.liull19.interview.mapper.resume.ResumeMapper;
import com.liull19.interview.mapper.resume.ResumeRepository;
import com.liull19.interview.model.resume.dto.ResumeAnalysisResponse;
import com.liull19.interview.model.resume.entity.ResumeAnalysisEntity;
import com.liull19.interview.model.resume.entity.ResumeEntity;
import com.liull19.interview.service.Resume.ResumeHistoryService;
import com.liull19.interview.utils.FileHashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

/**
 * 简历持久化服务 简历和评测结果的持久化，简历删除时删除所有关联数据
 */
@Slf4j
@Service
@RequiredArgsConstructor

public class ResumePersistenceService {

    private final ResumeRepository resumeRepository;
    private final ResumeAnalysisRepository analysisRepository;
    private final FileHashService fileHashService;
    private final ObjectMapper objectMapper;
    private final ResumeMapper resumeMapper;


    /**
     * 获取所有简历列表
     */
    public List<ResumeEntity> findAllResumes() {
        return resumeRepository.findAll();
    }

    /**
     * 获取简历的最新评测结果
     */
    public Optional<ResumeAnalysisEntity> getLatestAnalysis(Long resumeId) {
        return Optional.ofNullable(analysisRepository.findFirstByResumeIdOrderByAnalyzedAtDesc(resumeId));
    }

    /**
     * 检查简历是否已存在（基于文件内容hash）
     * Params: file – 上传的文件
     * Returns:  如果存在返回已有的简历实体，否则返回空
     */
    public Optional<ResumeEntity> findExistingResume(MultipartFile file) {
        try {
            String fileHash = fileHashService.calculateHash(file);
            Optional<ResumeEntity> existing = resumeRepository.findByFileHash(fileHash);
            if (existing.isPresent()){
                log.info("检测到重复简历: hash={}", fileHash);
                ResumeEntity resume = existing.get();
                resume.incrementAccessCount();
                resumeRepository.save(resume);
            }
            return existing;

        }catch (Exception e){
            log.error("检查简历重复时出错: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 获取简历的最新评测结果（返回DTO）
     */
    public Optional<ResumeAnalysisResponse> getLatestAnalysisAsDTO(Long resumeId) {
        return getLatestAnalysis(resumeId).map(this::entityToDTO);

    }

    /**
     * 将实体转换为DTO
     */
    public ResumeAnalysisResponse entityToDTO(ResumeAnalysisEntity entity) {
        try {
            List<String> strengths = objectMapper.readValue(
                    entity.getStrengthsJson() != null ? entity.getStrengthsJson() : "[]",
                    new TypeReference<>() {
                    }
            );

            List<ResumeAnalysisResponse.Suggestion> suggestions = objectMapper.readValue(
                    entity.getSuggestionsJson() != null ? entity.getSuggestionsJson() : "[]",
                    new TypeReference<>() {
                    }
            );

            return new ResumeAnalysisResponse(
                    entity.getOverallScore(),
                    resumeMapper.toScoreDetail(entity),  // 使用MapStruct自动映射
                    entity.getSummary(),
                    strengths,
                    suggestions,
                    entity.getResume().getResumeText()
            );
        } catch (JacksonException e) {
            log.error("反序列化评测结果失败: {}", e.getMessage());
            throw new BusinessException(ErrorCode.RESUME_ANALYSIS_FAILED, "获取评测结果失败");
        }
    }

    /**
     * 保存新简历
     */
    @Transactional(rollbackFor = Exception.class)
    public ResumeEntity saveResume(MultipartFile file, String resumeText, String storageKey, String storageUrl) {
        try {
            String fileHash = fileHashService.calculateHash(file);

            ResumeEntity resume = new ResumeEntity();
            resume.setFileHash(fileHash);
            resume.setOriginalFilename(file.getOriginalFilename());
            resume.setFileSize(file.getSize());
            resume.setContentType(file.getContentType());
            resume.setStorageKey(storageKey);
            resume.setStorageUrl(storageUrl);
            resume.setResumeText(resumeText);

            ResumeEntity saved = resumeRepository.save(resume);
            log.info("简历已保存: id={}, hash={}", saved.getId(), fileHash);

            return saved;
        } catch (Exception e) {
            log.error("保存简历失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.RESUME_UPLOAD_FAILED, "保存简历失败");
        }
    }

    /**
     * 根据ID获取简历
     */
    public Optional<ResumeEntity> findById(Long id) {
        return resumeRepository.findById(id);
    }

    /**
     * 获取简历的所有评测记录
     */
    public List<ResumeAnalysisEntity> findAnalysesByResumeId(Long resumeId) {
        return analysisRepository.findByResumeIdOrderByAnalyzedAtDesc(resumeId);
    }
}

