package com.gavin.cloud.disruptor;

import com.lmax.disruptor.LiteBlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class DisruptorTest {

    private static AtomicLong count = new AtomicLong(0L);

    public static void main(String[] args) throws InterruptedException {
        Disruptor<ObjectEvent> disruptor = new Disruptor<>(
                ObjectEvent::new,
                1024,
                Executors.defaultThreadFactory(),
                ProducerType.SINGLE,
                new LiteBlockingWaitStrategy()
        );

        disruptor.handleEventsWith((event, sequence, endOfBatch) -> log.debug("handler-1: {}", event.getObject()));
        disruptor.handleEventsWith((event, sequence, endOfBatch) -> log.debug("handler-2: {}", event.getObject()));

        disruptor.start();

        RingBuffer<ObjectEvent> ringBuffer = disruptor.getRingBuffer();
        while (true) {
            ringBuffer.publishEvent((event, sequence) -> event.setObject(count.incrementAndGet()));
            TimeUnit.SECONDS.sleep(1);
        }

    }

    static class ObjectEvent {

        private Object object;

        Object getObject() {
            return object;
        }

        void setObject(Object object) {
            this.object = object;
        }

    }

}
