package com.example.swissarmyknife;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) {
        Thread thread = new Thread();
        thread.start();
        thread.setPriority(2);
        long time = System.currentTimeMillis();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(System.currentTimeMillis() - time);

    }

    private static void testHashmapThresholdReflect() {
        HashMap hashMap = new HashMap(10);
        try {
            Field threshold = hashMap.getClass().getDeclaredField("threshold");
            threshold.setAccessible(true);
            System.out.println(threshold.get(hashMap));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 四个线程都要进行写操作，只有所有线程都完成写操作之后，这些线程才能继续做后面的事情
     */
    public static void testCyclicBarrier() {
        int workThreadNum = 4;
        CyclicBarrier barrier = new CyclicBarrier(workThreadNum);
        for (int i = 0; i < workThreadNum; i++) {
            if (i == workThreadNum - 1){
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            new Writer(barrier).start();

        }
    }

    static class Writer extends Thread {
        private CyclicBarrier mCyclicBarrier;

        public Writer(CyclicBarrier barrier) {
            mCyclicBarrier = barrier;
        }

        @Override
        public void run() {
            System.out.println("线程 " + Thread.currentThread().getName() + " 正在写入数据");
            try {
                Thread.sleep(2000); // 模拟写入数据
                System.out.println("线程 " + Thread.currentThread().getName() + " 写入数据完毕");
                mCyclicBarrier.await(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            System.out.println("线程 " + Thread.currentThread().getName() + "继续处理其他任务...");
        }
    }

    /*****************************************************
     * 一个工厂有5台机器，8个工人，一台机器同时只能被一个工人使用
     *****************************************************/
    public static void testSemaphore() {
        int workers = 8;
        Semaphore semaphore = new Semaphore(5); // 机器数目
        for (int i = 0; i < workers; i++) {
            new Worker(i, semaphore).start();
        }
    }

    static class Worker extends Thread {
        private int mWorkIndex;
        private Semaphore mSemaphore;

        public Worker(int workerIndex, Semaphore semaphore) {
            mWorkIndex = workerIndex;
            mSemaphore = semaphore;
        }

        @Override
        public void run() {
            try {
                mSemaphore.acquire();
                System.out.println("工人 " + mWorkIndex + " 占用一台机器生产... " + System.currentTimeMillis() / 1000);
                Thread.sleep(2000);
                synchronized (Main.class) {
                    System.out.println("工人 " + mWorkIndex + " 准备释放机器 " + System.currentTimeMillis() / 1000);
                    mSemaphore.release();
                    System.out.println("工人 " + mWorkIndex + " 释放了机器 " + System.currentTimeMillis() / 1000);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /*****************************************************
     * 测试List.subList——对sublist的操作会修改父list
     *****************************************************/
    public static void testAddPossibleResultPoint() {
        for (int i = 0; i < 20; i++) {
            addPossibleResultPoint("d");
        }
    }

    public static ArrayList<String> possibleResultPoints = new ArrayList<>(5);

    static {
        possibleResultPoints.add("a");
        possibleResultPoints.add("b");
        possibleResultPoints.add("c");
    }

    public static void addPossibleResultPoint(String point) {
        List<String> points = possibleResultPoints;
        points.add(point);
        int size = points.size();
        if (size > 20) {
            // trim it
            points.subList(0, size - 20 / 2).clear();
        }
        System.out.println(Arrays.toString(possibleResultPoints.toArray()));
    }
}
