package com.liull19.interview.mapper.resume;

import com.liull19.interview.model.resume.dto.ResumeAnalysisResponse;
import com.liull19.interview.model.resume.dto.ResumeDetailDTO;
import com.liull19.interview.model.resume.entity.ResumeAnalysisEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 简历相关的对象映射器
 * 使用MapStruct自动生成转换代码
 * <p>
 * 注意：JSON字段(strengthsJson, suggestionsJson)需要在Service层手动处理
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResumeMapper {


    /**
     * 将实体基础字段映射到DTO的ScoreDetail
     */
    @Mapping(target = "contentScore", source = "contentScore", qualifiedByName = "nullToZero")
    @Mapping(target = "structureScore", source = "structureScore", qualifiedByName = "nullToZero")
    @Mapping(target = "skillMatchScore", source = "skillMatchScore", qualifiedByName = "nullToZero")
    @Mapping(target = "expressionScore", source = "expressionScore", qualifiedByName = "nullToZero")
    @Mapping(target = "projectScore", source = "projectScore", qualifiedByName = "nullToZero")
    ResumeAnalysisResponse.ScoreDetail toScoreDetail(ResumeAnalysisEntity entity);


    /**
     * ResumeAnalysisEntity 转换为 AnalysisHistoryDTO
     * 注意：strengths 和 suggestions 需要在 Service 层从 JSON 解析后传入
     */
    @Mapping(target = "strengths", source = "strengths")
    @Mapping(target = "suggestions", source = "suggestions")
    ResumeDetailDTO.AnalysisHistoryDTO toAnalysisHistoryDTO(
            ResumeAnalysisEntity entity,
            List<String> strengths,
            List<Object> suggestions
    );
    /**
     * 批量转换（需要在 Service 层处理 JSON）
     */
    default List<ResumeDetailDTO.AnalysisHistoryDTO> toAnalysisHistoryDTOList(
            List<ResumeAnalysisEntity> entities,
            java.util.function.Function<ResumeAnalysisEntity, List<String>> strengthsExtractor,
            java.util.function.Function<ResumeAnalysisEntity, List<Object>> suggestionsExtractor
    ) {
        return entities.stream()
                .map(e -> toAnalysisHistoryDTO(e, strengthsExtractor.apply(e), suggestionsExtractor.apply(e)))
                .toList();
    }


    @Named("nullToZero")
    default int nullToZero(Integer value) {
        return value != null ? value : 0;
    }

}