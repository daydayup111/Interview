package com.liull19.interview.mapper.resume;

import com.liull19.interview.model.resume.entity.InterviewSessionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 面试相关的对象映射器
 * 使用MapStruct自动生成转换代码
 * <p>
 * 注意：JSON字段需要在Service层手动处理
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InterviewMapper {

    /**
     * InterviewSessionEntity 转换为简要信息 Map
     * 用于 ResumeDetailDTO 中的面试历史列表
     */
    default java.util.Map<String, Object> toInterviewHistoryItem(InterviewSessionEntity session) {
        java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
        map.put("id", session.getId());
        map.put("sessionId", session.getSessionId());
        map.put("totalQuestions", session.getTotalQuestions());
        map.put("status", session.getStatus().toString());
        map.put("evaluateStatus", session.getEvaluateStatus() != null ? session.getEvaluateStatus().name() : null);
        map.put("evaluateError", session.getEvaluateError());
        map.put("overallScore", session.getOverallScore());
        map.put("createdAt", session.getCreatedAt());
        map.put("completedAt", session.getCompletedAt());
        return map;
    }

    /**
     * 批量转换面试历史
     */
    default List<Object> toInterviewHistoryList(List<InterviewSessionEntity> sessions) {
        return sessions.stream()
                .map(this::toInterviewHistoryItem)
                .map(m -> (Object) m)
                .toList();
    }

    // ========== 工具方法 ==========

    @Named("nullIndexToZero")
    default int nullIndexToZero(Integer value) {
        return value != null ? value : 0;
    }

    @Named("nullScoreToZero")
    default int nullScoreToZero(Integer value) {
        return value != null ? value : 0;
    }

}
