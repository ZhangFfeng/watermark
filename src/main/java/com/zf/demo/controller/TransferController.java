package com.zf.demo.controller;

import com.zf.demo.service.TransferService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransferController {

    @Resource
    private TransferService transferService;

    @GetMapping("test")
    public String Test(){
        try {
            Long l1 = System.currentTimeMillis();
            transferService.transferPic();
            return (System.currentTimeMillis() - l1) + "";
        }catch (Exception e ){
            e.printStackTrace();
            return "9999999999999999999";
        }
    }
}