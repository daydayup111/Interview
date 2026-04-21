package com.liull19.interview.service.impl.Resume;

import com.liull19.interview.utils.ContentTypeDetectionService;
import com.liull19.interview.utils.DocumentParseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 简历解析服务
 * 委托给通用的 DocumentParseService 处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeParseService {

    private final ContentTypeDetectionService contentTypeDetectionService;
    private final DocumentParseService documentParseService;

    /**
     * 检测文件的MIME类型
     */
    public String detectContentType(MultipartFile file) {
        return contentTypeDetectionService.detectContentType(file);
    }

    /**
     * 解析上传的简历文件，提取文本内容
     *
     * @param file 上传的文件（支持PDF、DOCX、DOC、TXT、MD等）
     * @return 提取的文本内容
     */
    public String parseResume(MultipartFile file) {
        log.info("开始解析简历文件: {}", file.getOriginalFilename());
        return documentParseService.parseContent(file);
    }
}
