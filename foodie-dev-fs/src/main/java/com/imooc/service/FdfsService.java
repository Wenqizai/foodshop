package com.imooc.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Wenqi Liang
 * @date 2021/3/8
 */
public interface FdfsService {

    public String upload(MultipartFile file, String fileExtName) throws Exception;

    public String uploadOSS(MultipartFile file, String userId, String fileExtName) throws Exception;
}
