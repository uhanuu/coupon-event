package com.example.api.controller;

import com.example.api.service.ApplyService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CouponController {

    private final ApplyService applyService;

    public CouponController(ApplyService applyService) {
        this.applyService = applyService;
    }

    @PostMapping("/coupons")
    public void enduranceApplyCouponTestWithRedis() {
        applyService.applyWithRedis((long) ((Math.random() * 5000) + 1));
    }
}
