package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    SetmealService setmealService;

    @Autowired
    SetmealDishService setmealDishService;

    @Autowired
    CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){


        log.info("------------Set meal PLan:{}",setmealDto);

        setmealService.saveWithDish(setmealDto);
        return R.success("-----------Add new set-meal success!--------------");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){

        // 分页构造器
        Page<Setmeal> pageInfo =  new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,queryWrapper);
        //根据name 进行，like 模糊查询

        //添加排序条件，根据时间降序排列

        //拷贝对象从Setmeal to SetmealDto
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        //从record 里面找category id
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> ls = records.stream().map((item)->{
            SetmealDto dto = new SetmealDto();
            BeanUtils.copyProperties(item,dto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category !=null){
                dto.setCategoryName(category.getName());
            }
            return dto;
        }).collect(Collectors.toList());
        //我们的SetmealDto 里面没有category name 的值，从setmeal 里面找到

        dtoPage.setRecords(ls);

        return R.success(dtoPage);
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){

        log.info("{}",ids);
        setmealService.removeWithDish(ids);
        return R.success("Setmeal delete success!");
    }

    @GetMapping("/list")
    public R<List> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);


        return R.success(list);
    }

}
