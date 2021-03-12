package com.imooc.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.imooc.resource.FileResource;
import com.imooc.service.FdfsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author liangwq
 * @date 2021/3/8
 */
@Service
public class FdfsServiceImpl implements FdfsService {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    @Autowired
    private FileResource fileResource;

    @Override
    public String upload(MultipartFile file, String fileExtName) throws Exception {
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), fileExtName,
                null);
        String fullPath = storePath.getFullPath();
        return fullPath;
    }

    @Override
    public String uploadOSS(MultipartFile file, String userId, String fileExtName) throws IOException {
        // 构建ossClient
        OSS ossClient = new OSSClientBuilder().build(fileResource.getEndpoint(), fileResource.getAccessKeyId(), fileResource.getAccessKeySecret());

        InputStream inputStream = file.getInputStream();
        String myObjectName = fileResource.getObjectName() + "/" + userId + "/" + userId + "." + fileExtName;
        ossClient.putObject(fileResource.getBucketName(), myObjectName, inputStream);

        ossClient.shutdown();
        return myObjectName;
    }
}
