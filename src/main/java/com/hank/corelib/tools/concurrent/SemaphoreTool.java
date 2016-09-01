package com.hank.corelib.tools.concurrent;

import com.hank.corelib.logger.Logger;
import com.hank.corelib.tools.concurrent.base.AbsLimitCountExecutor;
import com.hank.corelib.tools.concurrent.base.BaseSemaphore;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 互斥作用，如果同时只能有5个线程访问统一资源，多余的线程来了之后会被阻塞掉
 * @param <T>
 */
public class SemaphoreTool<T> extends BaseSemaphore {


	public SemaphoreTool(int permits, boolean fair) {
		super(permits, fair);
	}

	public Object execute(AbsLimitCountExecutor executor) {
		ExecuteThread thread =new ExecuteThread(executor, semaphore);
		executorService.execute(thread);
		return null;
	}

	private final class ExecuteThread implements Runnable{

		private AbsLimitCountExecutor executor;
		private Semaphore semaphore;

		public ExecuteThread(AbsLimitCountExecutor executor,
							 Semaphore semaphore) {
			super();
			this.executor = executor;
			this.semaphore = semaphore;
		}

		public void run() {
			try {
				if(this.semaphore.tryAcquire(10, TimeUnit.SECONDS)){
					if(executor == null ){
						return;
					}
					executor.execute(this.executor.getData());
				}
			} catch (Exception e) {
				Logger.e(e);
			}finally{
				this.semaphore.release();
			}
		}
	}
}
