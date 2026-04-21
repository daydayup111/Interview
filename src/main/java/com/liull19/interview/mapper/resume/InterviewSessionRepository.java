package com.liull19.interview.mapper.resume;

import com.liull19.interview.model.resume.entity.InterviewAnswerEntity;
import com.liull19.interview.model.resume.entity.InterviewSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewSessionRepository extends JpaRepository<InterviewSessionEntity,Long> {

    //根据简历ID查找所有面试记录
    List<InterviewSessionEntity> findByResumeIdOrderByCreatedAtDesc(Long resumeId);
}
