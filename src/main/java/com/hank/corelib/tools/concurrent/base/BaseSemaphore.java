package com.hank.corelib.tools.concurrent.base;

import java.util.concurrent.Semaphore;


public abstract class BaseSemaphore extends BaseTask {
	
	
	protected Semaphore semaphore = null;
	public BaseSemaphore(int permits,boolean fair) {
		super(permits);
		//初始化信号量
		initSemaphore(permits, fair);
		
	}
	
	private void initSemaphore(int permits,boolean fair) {
		this.semaphore = new Semaphore(permits, fair);
	}

	
}
