package com.graphaware.importer.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class BlockingArrayBlockingQueue<E> extends ArrayBlockingQueue<E> {

    public BlockingArrayBlockingQueue(int capacity) {
        super(capacity);
    }

    @Override
    public boolean offer(E e) {
        try {
            return offer(e, 30, TimeUnit.MINUTES);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}

