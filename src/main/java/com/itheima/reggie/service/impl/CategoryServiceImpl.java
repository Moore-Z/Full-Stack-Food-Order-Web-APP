package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements CategoryService{

    @Autowired
    DishService dishService;

    @Autowired
    SetmealService setmealService;

    @Override
    public void remove(Long id) {
        //Find whether the category linked the dishes
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);

        int count_dish = dishService.count(dishLambdaQueryWrapper);

        if (count_dish>0) {
            throw new CustomException("Dish links existed in this Category");
        };

        //Find whether the catrgory linked the setmeal
        LambdaQueryWrapper<Setmeal> SetmealLambdaQueryWrapper = new LambdaQueryWrapper<>();

        SetmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);

        int Setmeal_count = setmealService.count(SetmealLambdaQueryWrapper);

        if (Setmeal_count>0) { throw new CustomException("Setmeal links existed in this Category");};
        //safely delete the category
        super.removeById(id);
    }
}
