package com.imooc.controller;

import com.imooc.pojo.Items;
import com.imooc.pojo.ItemsImg;
import com.imooc.pojo.ItemsParam;
import com.imooc.pojo.ItemsSpec;
import com.imooc.pojo.vo.CommentLevelCountsVO;
import com.imooc.pojo.vo.ItemInfoVO;
import com.imooc.service.ItemService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "商品接口", tags = "商品信息展示的相关接口")
@RestController
@RequestMapping("items")
public class ItemsController extends BaseController {

    final static Logger logger = LoggerFactory.getLogger(ItemsController.class);

    @Autowired
    private ItemService itemService;

    @ApiOperation(value = "查询商品详情", notes = "查询商品详情", httpMethod = "GET")
    @GetMapping("/info/{itemId}")
    public IMOOCJSONResult info(
            @ApiParam(name = "itemId", value = "商品id", required = true)
            @PathVariable("itemId") String itemId) {
        if (StringUtils.isBlank(itemId)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        Items item = itemService.queryItemById(itemId);
        List<ItemsImg> itemImgList = itemService.queryItemImgList(itemId);
        List<ItemsSpec> itemSpecList = itemService.queryItemSpecList(itemId);
        ItemsParam itemParams = itemService.queryItemParam(itemId);
        ItemInfoVO itemInfoVO = new ItemInfoVO();
        itemInfoVO.setItem(item);
        itemInfoVO.setItemImgList(itemImgList);
        itemInfoVO.setItemSpecList(itemSpecList);
        itemInfoVO.setItemParams(itemParams);
        return IMOOCJSONResult.ok(itemInfoVO);
    }

    @ApiOperation(value = "查询商品评价等级", notes = "查询商品评价等级", httpMethod = "GET")
    @GetMapping("/commentLevel")
    public IMOOCJSONResult commentLevel(
            @ApiParam(name = "itemId", value = "商品id", required = true)
            @RequestParam("itemId") String itemId) {
        if (StringUtils.isBlank(itemId)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        CommentLevelCountsVO countsVO = itemService.queryCommmentCounts(itemId);
        return IMOOCJSONResult.ok(countsVO);
    }

    @ApiOperation(value = "查询商品评价", notes = "查询商品评价", httpMethod = "GET")
    @GetMapping("/comments")
    public IMOOCJSONResult commentLevel(
            @ApiParam(name = "itemId", value = "商品id", required = true)
            @RequestParam(value = "itemId", required = true) String itemId,
            @ApiParam(name = "level", value = "评价等级")
            @RequestParam(value = "level", required = false) Integer level,
            @ApiParam(name = "page", value = "查询下一页的第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "pageSize", value = "分页的每页显示的条数")
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (StringUtils.isBlank(itemId)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            page = COMMENT_PAGE_SIZE;
        }
        PagedGridResult grid = itemService.queryPagedComments(itemId, level, page, pageSize);
        return IMOOCJSONResult.ok(grid);
    }

    @ApiOperation(value = "搜索商品列表", notes = "搜索商品列表", httpMethod = "GET")
    @GetMapping("/search")
    public IMOOCJSONResult search(
            @ApiParam(name = "keywords", value = "关键字", required = true)
            @RequestParam(value = "keywords", required = true) String keywords,
            @ApiParam(name = "sort", value = "排序")
            @RequestParam(value = "sort", required = false) String sort,
            @ApiParam(name = "page", value = "查询下一页的第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "pageSize", value = "分页的每页显示的条数")
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (StringUtils.isBlank(keywords)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }
        PagedGridResult grid = itemService.searchItems(keywords, sort, page, pageSize);
        return IMOOCJSONResult.ok(grid);
    }

    @ApiOperation(value = "根据商品分类id搜索商品列表", notes = "根据商品分类id搜索商品列表", httpMethod = "GET")
    @GetMapping("/catItems")
    public IMOOCJSONResult catItems(
            @ApiParam(name = "catId", value = "商品分类id", required = true)
            @RequestParam(value = "catId", required = true) Integer catId,
            @ApiParam(name = "sort", value = "排序")
            @RequestParam(value = "sort", required = false) String sort,
            @ApiParam(name = "page", value = "查询下一页的第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "pageSize", value = "分页的每页显示的条数")
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (catId == null) {
            return IMOOCJSONResult.errorMsg(null);
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }
        PagedGridResult grid = itemService.searchItems(catId, sort, page, pageSize);
        return IMOOCJSONResult.ok(grid);
    }

}
