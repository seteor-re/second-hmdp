package com.hmdp.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.hmdp.utils.RedisConstants.CACHE_SHOP_TYPE_KEY;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    
    @Override
    public Result queryTypeList() {
        String key = CACHE_SHOP_TYPE_KEY;
        List<String> stringTypeList = stringRedisTemplate.opsForList().range(key, 0, -1);
        //判断是否命中
        if (stringTypeList != null && !stringTypeList.isEmpty()) {
            //如果命中则直接返回
            List<ShopType> shopTypeList = stringTypeList.stream()
                    .map(jsonStr -> JSONUtil.toBean(jsonStr, ShopType.class))
                    .collect(Collectors.toList());
            return Result.ok(shopTypeList);
        }
        //如果不命中则取数据库中搜索
        List<ShopType> shopTypeList = query().orderByAsc("sort").list();
        //如果数据库中没有则返回错误
        if (shopTypeList.isEmpty()) {
            return Result.fail("店铺类型不存在");
        }
        //如果数据库中有则添加到redis中
        stringTypeList = shopTypeList.stream()
                .map(JSONUtil::toJsonStr)
                .toList();
        stringRedisTemplate.opsForList().rightPushAll(key, stringTypeList);
        //返回
        return Result.ok(shopTypeList);
    }
}
