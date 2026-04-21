package com.liull19.interview.model.resume.entity;

import com.liull19.interview.constant.AsyncTaskStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 简历实体
 * @Entity：表示这是 JPA 实体类
 * @Table(name = "resumes")：对应数据库表名
 * @Index：给 fileHash 建唯一索引，加速去重查询
 * @Id + @GeneratedValue：主键自增
 * @Column：定义数据库字段属性（非空、长度、唯一）
 * @PrePersist：插入数据前自动执行，设置上传时间、访问次数
 * @Enumerated(EnumType.STRING)：把枚举存成字符串（如 PENDING/COMPLETED）
 */
@Data
@Entity
@Table(name = "resumes", indexes = {
    @Index(name = "idx_resume_hash", columnList = "fileHash", unique = true)
})
public class ResumeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 文件内容的SHA-256哈希值，用于去重
    @Column(nullable = false, unique = true, length = 64)
    private String fileHash;
    
    // 原始文件名
    @Column(nullable = false)
    private String originalFilename;
    
    // 文件大小（字节）
    private Long fileSize;
    
    // 文件类型
    private String contentType;
    
    // RustFS存储的文件Key
    @Column(length = 500)
    private String storageKey;
    
    // RustFS存储的文件URL
    @Column(length = 1000)
    private String storageUrl;
    
    // 解析后的简历文本
    @Column(columnDefinition = "TEXT")
    private String resumeText;
    
    // 上传时间
    @Column(nullable = false)
    private LocalDateTime uploadedAt;
    
    // 最后访问时间
    private LocalDateTime lastAccessedAt;
    
    // 访问次数
    private Integer accessCount = 0;

    // 分析状态（新上传时为 PENDING，异步分析完成后变为 COMPLETED）
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AsyncTaskStatus analyzeStatus = AsyncTaskStatus.PENDING;

    // 分析错误信息（失败时记录）
    @Column(length = 500)
    private String analyzeError;
    
    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
        lastAccessedAt = LocalDateTime.now();
        accessCount = 1;
    }
    
    // Getters and Setters
    public void incrementAccessCount() {
        this.accessCount++;
        this.lastAccessedAt = LocalDateTime.now();
    }


}
