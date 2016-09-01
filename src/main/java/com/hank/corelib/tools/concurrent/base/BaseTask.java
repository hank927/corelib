package com.hank.corelib.tools.concurrent.base;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseTask {

	protected static volatile int totalThreadCount = 0;
	protected volatile int threadCount = 0;
	protected final static int cpuCoreCount = Runtime.getRuntime().availableProcessors();
	protected final static ExecutorService executorService =  Executors.newFixedThreadPool(2*cpuCoreCount);
	
	public BaseTask(int permits) {
		//初始化线程池
		initThreadPool(permits);
		
	}
	
	
	private void initThreadPool(int permits) {
		threadCount = permits;
	}
	
	/**
	 * 执行方法
	 * @param executor
	 * @return
	 */
	public abstract Object execute(AbsLimitCountExecutor executor);
}
