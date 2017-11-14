package org.ditw.rschr.utils;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by dev on 2017-11-14.
 */
public class DisruptorTests {

    private static void handleEvent(ValEvent evt, long seq, boolean eob) {
        System.out.println("Handled: " + evt.getv());
    }

    private static void translateEvent(ValEvent evt, long seq, ByteBuffer buf) {
        evt.setv(buf.getLong(0));
    }

    public static void main(String[] args) throws Exception {

        Executor executor = Executors.newCachedThreadPool();

        int bufSize = 1024;

        ThreadFactory threadFactory = DaemonThreadFactory.INSTANCE;
        Disruptor<ValEvent> disruptor = new Disruptor<>(
            ValEvent.EVT_FACTORY,
            bufSize,
            threadFactory,
            ProducerType.SINGLE,
            new BlockingWaitStrategy()
        );

        disruptor.handleEventsWith(DisruptorTests::handleEvent);
        disruptor.start();

        RingBuffer<ValEvent> ringBuf = disruptor.getRingBuffer();

        ByteBuffer bb = ByteBuffer.allocate(8);

        for (long l = 0; true; l++) {
            bb.putLong(0, l);
            ringBuf.publishEvent(DisruptorTests::translateEvent, bb);
            Thread.sleep(100);
        }
    }
}
