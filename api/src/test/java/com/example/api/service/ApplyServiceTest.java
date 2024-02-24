package com.example.api.service;

import com.example.api.repository.CouponRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ApplyServiceTest {

    @Autowired
    private ApplyService applyService;

    @Autowired
    private CouponRepository couponRepository;

    @Test
    public void 한번만_응모() {
        //when
        applyService.apply(1L);
        //then
        long count = couponRepository.count();
        assertThat(count).isEqualTo(1);
    }

    @Test
    public void 여러명_응모() throws InterruptedException {
        // given
        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        // 다른 스레드에서 수행하는 작업을 기다리도록 도와주는 클래스
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            long userId = i;
            executorService.submit(() -> {
                try {
                    applyService.apply(userId);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        // then
        long count = couponRepository.count();
        //레이스 컨디션으로 인해서 100개 이상의 쿠폰이 발급된다.
        assertThat(count).isNotEqualTo(100);
    }

    // 테스트할 때마다 redis 키값을 0으로 초기화 해줘야
    @Test
    public void 레디스를_이용한_여러명_응모() throws InterruptedException {
        // given
        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            long userId = i;
            executorService.submit(() -> {
                try {
                    applyService.applyWithRedis(userId);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        // then
        long count = couponRepository.count();
        assertThat(count).isEqualTo(100);
    }


}