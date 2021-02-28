package com.imooc.controller;

import com.imooc.enums.YesOrNo;
import com.imooc.pojo.Carousel;
import com.imooc.pojo.Category;
import com.imooc.pojo.vo.CategoryVO;
import com.imooc.pojo.vo.NewItemsVO;
import com.imooc.service.CarouselService;
import com.imooc.service.CategoryService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(value = "首页", tags = "首页展示的相关接口")
@RestController
@RequestMapping("index")
public class IndexController {

    final static Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private CarouselService carouselService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "获取首页轮播图列表", notes = "获取首页轮播图列表", httpMethod = "GET")
    @GetMapping("/carousel")
    public IMOOCJSONResult carousel() {
        String carousels = redisOperator.get("carousel");
        List<Carousel> list = new ArrayList<>();
        if (StringUtils.isBlank(carousels)) {
            list = carouselService.queryAll(YesOrNo.YES.type);
            if (list != null && list.size() > 0) {
                redisOperator.set("carousel", JsonUtils.objectToJson(list));
            } else {
                redisOperator.set("carousel", JsonUtils.objectToJson(list), 5 * 60);
            }
        } else {
            list = JsonUtils.jsonToList(carousels, Carousel.class);
        }
        return IMOOCJSONResult.ok(list);
    }
    /**
     * 1. 后台营运系统, 一旦广告(轮播图)发生更改, 就可以删除缓存, 然后重置
     * 2. 定时重置, 比如每天凌晨三点重置
     * 3. 每个轮播图都有可能是一个广告, 每个广告都会有一个过期时间, 过期了, 再重置
     */

    /**
     * 首页分类展示的需求:
     * 1. 第一次刷新主页查询大分类, 渲染展示到首页
     * 2. 如果鼠标上移到大分类, 则加载其子分类的内容, 如果已经存在子分类, 则不需要加载(懒加载).
     */
    @ApiOperation(value = "获取商品的分类(一级分类)", notes = "获取商品的分类(一级分类)", httpMethod = "GET")
    @GetMapping("/cats")
    public IMOOCJSONResult category() {
        String category = redisOperator.get("Category");
        List<Category> categoryList = new ArrayList<>();
        if (StringUtils.isBlank(category)) {
            categoryList = categoryService.queryAllRootLevelCat();
            if (categoryList != null && categoryList.size() > 0) {
                redisOperator.set("Category", JsonUtils.objectToJson(categoryList));
            } else {
                redisOperator.set("Category", JsonUtils.objectToJson(categoryList), 5 * 60);
            }
        } else {
            categoryList = JsonUtils.jsonToList(category, Category.class);
        }
        return IMOOCJSONResult.ok(categoryList);
    }

    @ApiOperation(value = "获取商品的子分类", notes = "获取商品的子分类", httpMethod = "GET")
    @GetMapping("/subCat/{rootCatId}")
    public IMOOCJSONResult subCat(
            @ApiParam(name = "rootCatId", value = "一级分类id", required = true)
            @PathVariable("rootCatId") Integer rootCatId) {
        if (rootCatId == null) {
            return IMOOCJSONResult.errorMsg("分类不存在");
        }
        String subCats = redisOperator.get("subCat:" + rootCatId);
        List<CategoryVO> subCatList;
        if (StringUtils.isBlank(subCats)) {
            subCatList = categoryService.getSubCatList(rootCatId);
            /**
             * 查询的key在redis中不存在
             * 对应的id在数据库也不存在
             * 此时被非法用户进行攻击, 大量的请求会直接打在db上, 造成宕机, 从而影响整个系统
             * 这种现象称之为缓存传统
             * 解决方案: 把空的数据也缓存起来, 比如空字符串, 空对象, 空数组或list
             */
            if (subCatList != null && subCatList.size() > 0) {
                redisOperator.set("subCat:" + rootCatId, JsonUtils.objectToJson(subCatList));
            } else {
                redisOperator.set("subCat:" + rootCatId, JsonUtils.objectToJson(subCatList), 5 * 60);
            }
        } else {
            subCatList = JsonUtils.jsonToList(subCats, CategoryVO.class);
        }
        return IMOOCJSONResult.ok(subCatList);
    }

    @ApiOperation(value = "查询每个一级分类下的最新6条商品数据", notes = "查询每个一级分类下的最新6条商品数据", httpMethod = "GET")
    @GetMapping("/sixNewItems/{rootCatId}")
    public IMOOCJSONResult sixNewItems(
            @ApiParam(name = "rootCatId", value = "一级分类id", required = true)
            @PathVariable("rootCatId") Integer rootCatId) {
        if (rootCatId == null) {
            return IMOOCJSONResult.errorMsg("分类不存在");
        }
        String key = "sixNewItems:" + rootCatId;
        String sixNewItems = redisOperator.get(key);
        List<NewItemsVO> sixNewItemsList = new ArrayList<>();
        if (StringUtils.isBlank(sixNewItems)) {
            sixNewItemsList = categoryService.getSixNewItemsLazy(rootCatId);
            if (sixNewItemsList != null && sixNewItemsList.size() > 0) {
                redisOperator.set(key, JsonUtils.objectToJson(sixNewItemsList));
            } else {
                redisOperator.set(key, JsonUtils.objectToJson(sixNewItemsList), 5 * 60);
            }
        } else {
            sixNewItemsList = JsonUtils.jsonToList(sixNewItems, NewItemsVO.class);
        }
        return IMOOCJSONResult.ok(sixNewItemsList);
    }

}
