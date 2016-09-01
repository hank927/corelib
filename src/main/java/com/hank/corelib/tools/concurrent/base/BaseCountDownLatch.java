package com.hank.corelib.tools.concurrent.base;

import java.util.concurrent.CountDownLatch;


public abstract class BaseCountDownLatch extends BaseTask {
	
	
	protected CountDownLatch countDownLatch = null;
	public BaseCountDownLatch(CountDownLatch countDownLatch,int count) {
		super(count);
		this.countDownLatch = countDownLatch;
	}

}
