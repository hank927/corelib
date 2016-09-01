package com.hank.corelib.tools.concurrent;

import com.hank.corelib.tools.concurrent.base.AbsLimitCountExecutor;
import com.hank.corelib.tools.concurrent.base.BaseCountDownLatch;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

/**
 * Created by hank on 2016/9/1.
 */
public class CountDownLatchToolTest {
    static CountDownLatch countDownLatch = new CountDownLatch(5);
    static BaseCountDownLatch taskExecutorUtil = new CountDownLatchTool(
            countDownLatch, 5);
    @Test
    public void testExecute() throws Exception {
        for (int i = 0; i < 5; i++) {
            final String name = "name:"+i;
            new Thread(new Runnable() {

                public void run() {
                    // TODO Auto-generated method stub
                    TestEntity testEntity = new TestEntity();
                    testEntity.setName(name);
                    taskExecutorUtil
                            .execute(new AbsLimitCountExecutor<TestEntity>(
                                    testEntity) {
                                public Object execute(TestEntity testEntity) {
                                    // TODO Auto-generated method stub
                                    System.out.println("thread-name:"
                                            + Thread.currentThread()
                                            + ",testEntity"
                                            + testEntity.getName());
                                    return null;
                                }

                            });
                }
            }).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("over");
    }
}