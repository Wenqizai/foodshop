package com.imooc.controller;

import com.imooc.service.StuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author liangwq
 * @date 2020/11/29
 */
@Controller
public class BaseController {

    public static final Integer COMMENT_PAGE_SIZE = 10;

}
