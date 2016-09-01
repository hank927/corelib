package com.hank.corelib.tools.concurrent;

import com.hank.corelib.tools.concurrent.base.AbsLimitCountExecutor;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hank on 2016/9/1.
 */
public class CountDownLatchToolsTest {
    static CountDownLatchTools taskExecutorUtil = new CountDownLatchTools();
    @Test
    public void testExecute() throws Exception {

        for (int i = 0; i < 5; i++) {
            final String name = "name:"+i;
            TestEntity testEntity = new TestEntity();
            testEntity.setName(name);
            taskExecutorUtil
                    .AddExecutor(new AbsLimitCountExecutor<TestEntity>(
                            testEntity) {
                        public Object execute(TestEntity testEntity) {
                            // TODO Auto-generated method stub
                            System.out.println("thread-name:"
                                    + Thread.currentThread() + ",testEntity"
                                    + testEntity.getName());
                            return null;
                        }

                    });
        }
        taskExecutorUtil.execute();

        System.out.println("over");
    }
}