package com.imooc.controller.center;

import com.imooc.controller.BaseController;
import com.imooc.enums.YesOrNo;
import com.imooc.pojo.OrderItems;
import com.imooc.pojo.Orders;
import com.imooc.pojo.bo.center.OrderItemsCommentBO;
import com.imooc.service.center.MyCommentService;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liangwq
 * @date 2021/1/9
 */
@Api(value = "用户中心评价模块", tags = {"用户中心评价模块相关的api接口"})
@RequestMapping("mycomments")
@RestController
public class MyCommentController extends BaseController {

    @Autowired
    private MyCommentService myCommentService;

    @ApiOperation(value = "获取未评价的商品信息", notes = "获取未评价的商品信息", httpMethod = "POST")
    @PostMapping("/pending")
    public IMOOCJSONResult pending(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId) {
        // 检验用户和订单是否关联
        IMOOCJSONResult result = checkUserOrder(userId, orderId);
        if (result.getStatus() != HttpStatus.OK.value()) {
            return result;
        }
        // 判断该笔订单是否已经评价过, 评价过了就不在继续
        Orders myOrder = (Orders) result.getData();
        if (myOrder.getIsComment().equals(YesOrNo.YES.type)) {
            return IMOOCJSONResult.errorMsg("该订单已经评价");
        }
        List<OrderItems> list = myCommentService.queryPendingComment(orderId);
        return IMOOCJSONResult.ok(list);
    }

    @ApiOperation(value = "保存评论列表", notes = "保存评论列表", httpMethod = "POST")
    @PostMapping("/saveList")
    public IMOOCJSONResult saveList(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId,
            @RequestBody List<OrderItemsCommentBO> commentList) {
        System.out.println(commentList);
        // 检验用户和订单是否关联
        IMOOCJSONResult result = checkUserOrder(userId, orderId);
        if (result.getStatus() != HttpStatus.OK.value()) {
            return result;
        }
        // 判断评论内容list不能为空
        if (commentList == null || commentList.isEmpty() || commentList.size() == 0) {
            return IMOOCJSONResult.errorMsg("评论内容不能为空!");
        }
        myCommentService.saveComments(orderId, userId, commentList);
        return IMOOCJSONResult.ok();
    }


}
