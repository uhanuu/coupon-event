package com.example.api.service;

import com.example.api.domain.Coupon;
import com.example.api.repository.CouponCountRepository;
import com.example.api.repository.CouponRepository;
import org.springframework.stereotype.Service;

@Service
public class ApplyService {

    private final CouponRepository couponRepository;
    private final CouponCountRepository couponCountRepository;

    public ApplyService(CouponRepository couponRepository, CouponCountRepository couponCountRepository) {
        this.couponRepository = couponRepository;
        this.couponCountRepository = couponCountRepository;
    }

    public void apply(Long userId) {
        // synchronized는 서버가 여러대면 의미가 없음
        // Lock으로 제어하는게 아니라 쿠폰 개수의 정합성만 맞추면 될듯하다.
        long count = couponRepository.count();

        if (count > 100) {
            return;
        }
        couponRepository.save(new Coupon(userId));
    }


    public void applyWithRedis(Long userId) {
        //redis incr key에 대한 value를 1증가 (redis는 singleThread로 동작함)
        long count = couponCountRepository.increment();

        if (count > 100) {
            return;
        }

        couponRepository.save(new Coupon(userId));
    }
}
