package com.test;

import com.imooc.service.StuService;
import com.imooc.service.TestTransService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author liangwq
 * @date 2020/12/2
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = Application.class)
public class TransTest {

    @Autowired
    private StuService stuService;
    @Autowired
    private TestTransService testTransService;

    @Test
    public void myTest() {
        testTransService.testPropagationTrans();
    }
}
