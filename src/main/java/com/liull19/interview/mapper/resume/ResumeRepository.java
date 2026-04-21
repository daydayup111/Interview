package com.liull19.interview.mapper.resume;

import com.liull19.interview.model.resume.entity.ResumeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 简历Repository
 */
@Repository
public interface ResumeRepository extends JpaRepository<ResumeEntity,Long> {

    /**
     * 根据文件哈希查找简历（用于去重）
     */
    Optional<ResumeEntity> findByFileHash(String fileHash);
}
