package com.liull19.interview.service.impl.Resume;

/**
 * 面试持久化服务 面试会话和答案的持久化
 */

import com.liull19.interview.mapper.resume.InterviewSessionRepository;
import com.liull19.interview.model.resume.entity.InterviewSessionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InterviewPersistenceService {

    private final InterviewSessionRepository sessionRepository;


    /**
     * 获取简历的所有面试记录
     */
    public List<InterviewSessionEntity> findByResumeId(Long resumeId) {
        return sessionRepository.findByResumeIdOrderByCreatedAtDesc(resumeId);
    }
}
