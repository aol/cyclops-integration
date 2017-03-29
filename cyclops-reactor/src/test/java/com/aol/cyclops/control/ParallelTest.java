package com.aol.cyclops.control;

import cyclops.async.QueueFactories;

import java.util.stream.Stream;


public class ParallelTest {
    public static void main(String args[])
    {
    int cores = Runtime.getRuntime().availableProcessors();
    System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", String.valueOf(cores*4));

        for(int k=0; k < 10;k++) {

            cyclops.async.Queue<Integer> queue = QueueFactories.<Integer>boundedQueue(5000).build();

            new Thread(() -> {
                while(queue.isOpen()){
                    System.err.println(queue.close());
                }
            }).start();

            Stream<Integer> stream = queue.jdkStream();

            stream = stream.parallel();
            stream.forEach(e ->
            {
                System.out.println(e);
            });
            System.out.println("done " + k);
        }
    }
}
