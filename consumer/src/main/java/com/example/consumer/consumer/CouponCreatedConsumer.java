package com.example.consumer.consumer;

import com.example.consumer.domain.Coupon;
import com.example.consumer.domain.FailedEvent;
import com.example.consumer.repository.CouponRepository;
import com.example.consumer.repository.FailedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CouponCreatedConsumer {

    private final CouponRepository couponRepository;
    private final FailedEventRepository failedEventRepository;

    private final Logger logger = LoggerFactory.getLogger(CouponCreatedConsumer.class);

    public CouponCreatedConsumer(CouponRepository couponRepository, FailedEventRepository failedEventRepository) {
        this.couponRepository = couponRepository;
        this.failedEventRepository = failedEventRepository;
    }

    // 카프카는 queue에 data가 있기 때문에 DB 데이터 처리량을 조절할 수 있다.
    // 쿠폰 생성까지 텀이 존재한다는 단점
    @KafkaListener(topics = "coupon_create", groupId = "group_1")
    public void listener(Long userId) {
        try {
            couponRepository.save(new Coupon(userId));
        } catch (Exception e) {
            // 쿠폰을 저장하다가 실패하면 배치 프로그램을 통해서 failedEventRepository에 저장된 쿠폰 재갱신 해주기
            logger.error("failed to create coupon={}", userId);
            failedEventRepository.save(new FailedEvent(userId));
        }
    }
}
