package com.hank.corelib.tools.concurrent.base;

import java.util.concurrent.CyclicBarrier;

public abstract class BaseCyclicBarrier extends BaseTask {

	protected CyclicBarrier cyclicBarrier;
	
	public BaseCyclicBarrier(int permits) {
		super(permits);
		initCyclicBarrier();
	}

	private void initCyclicBarrier() {
		// TODO Auto-generated method stub
		cyclicBarrier = new CyclicBarrier(threadCount);
	}

}
