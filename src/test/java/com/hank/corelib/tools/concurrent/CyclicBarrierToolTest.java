package com.hank.corelib.tools.concurrent;

import com.hank.corelib.tools.concurrent.base.AbsLimitCountExecutor;
import com.hank.corelib.tools.concurrent.base.BaseCyclicBarrier;
import com.hank.corelib.util.TimeUtils;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

/**
 * Created by hank on 2016/9/1.
 */
public class CyclicBarrierToolTest {
    static BaseCyclicBarrier taskExecutorUtil = new CyclicBarrierTool(3);
    @Test
    public void testExecute() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(6);
        for (int i = 0; i < 6; i++) {
            final String name = "name:"+i;
            new Thread(new Runnable() {

                public void run() {
                    // TODO Auto-generated method stub
                    TestEntity testEntity = new TestEntity();
                    testEntity.setName(name);
                    taskExecutorUtil.execute(new AbsLimitCountExecutor<TestEntity>(testEntity) {
                        public Object execute(TestEntity testEntity) {
                            // TODO Auto-generated method stub
                            System.out.println("thread-name:"+Thread.currentThread()+",testEntity "+testEntity.getName());
                            countDownLatch.countDown();
                            return null;
                        }

                    });
                }
            }).start();

        }
        countDownLatch.await();
    }
}