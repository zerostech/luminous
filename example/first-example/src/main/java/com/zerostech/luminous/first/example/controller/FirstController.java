package com.zerostech.luminous.first.example.controller;

import com.zerostech.luminous.common.response.Result;
import com.zerostech.luminous.web.Lu;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description
 *
 * @author 迹_Jason
 *
 * @create 2025-01-06 22:17
 **/
@RestController
@RequestMapping("first")
public class FirstController {

    @GetMapping
    public Result<Void> getFirstData() {
        Lu.basic.cache.set("111", "1111");
        return Result.success();
    }
}
