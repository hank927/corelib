package com.hank.corelib.tools.concurrent;

import android.os.Looper;
import android.util.Log;

import com.hank.corelib.logger.Logger;
import com.hank.corelib.tools.concurrent.base.AbsLimitCountExecutor;
import com.hank.corelib.tools.concurrent.base.BaseSemaphore;
import com.hank.corelib.util.TimeUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

/**
 * Created by hank on 2016/9/1.
 * desc:测试运行20个线程，抢占资源
 */
public class SemaphoreToolTest {
    static BaseSemaphore taskExecutorUtil = new SemaphoreTool<TestEntity>(3, true);
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testExecute() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(20);
        for (int i = 0; i < 20; i++) {
            final String name = "name:"+ i;
            new Thread(new Runnable() {

                public void run() {
                    TestEntity testEntity = new TestEntity();
                    testEntity.setName(name);
                    System.out.println(testEntity.getName());
                    taskExecutorUtil.execute(new AbsLimitCountExecutor<TestEntity>(testEntity) {
                        public Object execute(TestEntity testEntity) {
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