package com.liull19.interview.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 文件内容类型检测服务
 * 使用 Apache Tika 进行精确的 MIME 类型检测
 */
@Slf4j
@Service
public class ContentTypeDetectionService {

    private final Tika tika;

    public ContentTypeDetectionService() {
        this.tika = new Tika();
    }
    /**
     * 检测文件的 MIME 类型 使用 Tika 进行基于内容的检测，比 HTTP 头部更准确
     * Params: file – MultipartFile 文件
     * Returns: MIME 类型字符串
     * */
    public String detectContentType(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()){
            return tika.detect(inputStream, file.getOriginalFilename());
        }catch (Exception e){
            log.warn("无法检测文件类型，使用 Content-Type 头部: {}", e.getMessage());
            return file.getContentType();
        }
    }
}
