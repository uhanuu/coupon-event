package com.example.api.service;

import com.example.api.domain.Coupon;
import com.example.api.producer.CouponCreateProducer;
import com.example.api.repository.AppliedUserRepository;
import com.example.api.repository.CouponCountRepository;
import com.example.api.repository.CouponRepository;
import org.springframework.stereotype.Service;

@Service
public class ApplyService {

    private final CouponRepository couponRepository;
    private final CouponCountRepository couponCountRepository;
    private final CouponCreateProducer couponCreateProducer;
    private final AppliedUserRepository appliedUserRepository;

    public ApplyService(CouponRepository couponRepository,
                        CouponCountRepository couponCountRepository,
                        CouponCreateProducer couponCreateProducer,
                        AppliedUserRepository appliedUserRepository
    ) {
        this.couponRepository = couponRepository;
        this.couponCountRepository = couponCountRepository;
        this.couponCreateProducer = couponCreateProducer;
        this.appliedUserRepository = appliedUserRepository;
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

    public void applyWithKafka(Long userId) {
        //redis incr key에 대한 value를 1증가 (redis는 singleThread로 동작함)
        long count = couponCountRepository.increment();

        if (count > 100) {
            return;
        }

        couponCreateProducer.create(userId);
    }

    public void applyWithKafkaAndRedisSetForUser(Long userId) {
        //redis set 자료구조로 사람 1명당 쿠폰 1개만 가능하게 처리하기 (반환값이 1이 나오면 처음 등록 0이면 중복 등록)
        long apply = appliedUserRepository.add(userId);

        if (apply != 1) {
            //원래는 예외 던지는게 맞음
            return;
        }

        long count = couponCountRepository.increment();

        if (count > 100) {
            return;
        }

        couponCreateProducer.create(userId);
    }
}
