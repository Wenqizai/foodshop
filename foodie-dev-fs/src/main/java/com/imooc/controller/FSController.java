package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UserVO;
import com.imooc.resource.FileResource;
import com.imooc.service.FdfsService;
import com.imooc.service.center.CenterUserService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.DateUtil;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author liangwq
 * @date 2021/1/9
 */
@RequestMapping("/fdfs")
@RestController
public class FSController extends BaseController {

    @Autowired
    private FdfsService fdfsService;
    @Autowired
    private FileResource fileResource;
    @Autowired
    private CenterUserService centerUserService;

    @PostMapping("/uploadFace")
    public IMOOCJSONResult uploadFace(@RequestParam String userId,
                                      MultipartFile file,
                                      HttpServletRequest request, HttpServletResponse response) {
        String path = "";
        // 开始文件上传
        if (file != null) {
            // 获取文件上传的文件名
            String filename = file.getOriginalFilename();
            if (StringUtils.isNotBlank(filename)) {
                // 文件重命名
                // 保存格式: face-{userId}.png
                String[] fileNameArray = filename.split("\\.");
                // 获取文件后缀名
                String suffix = fileNameArray[fileNameArray.length - 1];

                if ("png".equalsIgnoreCase(suffix) &&
                        "gif".equalsIgnoreCase(suffix) &&
                        "jpg".equalsIgnoreCase(suffix) &&
                        "jpeg".equalsIgnoreCase(suffix)) {
                    return IMOOCJSONResult.errorMsg("图片格式不正确");
                }
                try {
//                    path = fdfsService.upload(file, suffix);
                    path = fdfsService.uploadOSS(file, userId, suffix);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            return IMOOCJSONResult.errorMsg("文件不能为空!");
        }

        if (StringUtils.isNotBlank(path)) {
            // 获取上传服务器后的url地址
//            String finalUserFaceUrl = fileResource.getHost() + path;
            String finalUserFaceUrl = fileResource.getOssHost() + path;
            // 更新头像至数据库
            Users userResult = centerUserService.updateUserFace(userId, finalUserFaceUrl);
            // 后续要改, 增加令牌token, 会整合到Redis中, 分布式会话
            UserVO userVO = conventUserVO(userResult);
            CookieUtils.setCookie(request, response, "user",
                    JsonUtils.objectToJson(userVO), true);
        } else {
            return IMOOCJSONResult.errorMsg("上传头像失败");
        }
        return IMOOCJSONResult.ok();
    }

}
