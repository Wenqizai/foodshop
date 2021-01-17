package com.imooc.controller.center;

import com.imooc.controller.BaseController;
import com.imooc.pojo.Orders;
import com.imooc.service.center.MyOrdersService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @author liangwq
 * @date 2021/1/9
 */
@Api(value = "用户中心我的订单", tags = {"用户中心我的订单相关的api接口"})
@RequestMapping("myorders")
@RestController
public class MyOrdersController extends BaseController {

    @Autowired
    private MyOrdersService myOrdersService;

    @ApiOperation(value = "查询订单列表", notes = "查询订单列表", httpMethod = "GET")
    @PostMapping("/query")
    public IMOOCJSONResult query(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderStatus", value = "订单状态", required = false)
            @RequestParam Integer orderStatus,
            @ApiParam(name = "page", value = "查询下一页的第几页")
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每页显示的条数")
            @RequestParam Integer pageSize) {
        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            page = COMMENT_PAGE_SIZE;
        }
        PagedGridResult grid = myOrdersService.queryMyOrders(userId, orderStatus, page, pageSize);
        return IMOOCJSONResult.ok(grid);
    }

    @ApiOperation(value = "商家发货", notes = "商家发货", httpMethod = "GET")
    @GetMapping("/deliver")
    public IMOOCJSONResult query(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId) {
        if (StringUtils.isBlank(orderId)) {
            return IMOOCJSONResult.errorMsg("订单ID不能为空!");
        }
        myOrdersService.updateDeliverOrderStatus(orderId);
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户确认收货", notes = "用户确认收货", httpMethod = "POST")
    @PostMapping("/confirmReceive")
    public IMOOCJSONResult confirmReceivequery(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId) {
        if (StringUtils.isBlank(orderId)) {
            return IMOOCJSONResult.errorMsg("订单ID不能为空!");
        }
        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg("用户ID不能为空!");
        }
        IMOOCJSONResult result = checkUserOrder(userId, orderId);
        if (result.getStatus() != HttpStatus.OK.value()) {
            return result;
        }
        myOrdersService.queryMyOrder(userId, orderId);
        boolean res = myOrdersService.updateReceiveOrderStatus(orderId);
        if (!res) {
            return IMOOCJSONResult.errorMsg("订单确认收货失败!");
        }
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户删除订单", notes = "用户删除订单", httpMethod = "POST")
    @PostMapping("/delete")
    public IMOOCJSONResult delete(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId) {
        if (StringUtils.isBlank(orderId)) {
            return IMOOCJSONResult.errorMsg("订单ID不能为空!");
        }
        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg("用户ID不能为空!");
        }
        IMOOCJSONResult result = checkUserOrder(userId, orderId);
        if (result.getStatus() != HttpStatus.OK.value()) {
            return result;
        }
        myOrdersService.queryMyOrder(userId, orderId);
        boolean res = myOrdersService.deleteReceiveOrderStatus(userId, orderId);
        if (!res) {
            return IMOOCJSONResult.errorMsg("订单删除失败!");
        }
        return IMOOCJSONResult.ok();
    }


}
