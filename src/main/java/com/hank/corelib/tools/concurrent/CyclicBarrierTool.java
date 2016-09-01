package com.hank.corelib.tools.concurrent;

import com.hank.corelib.logger.Logger;
import com.hank.corelib.tools.concurrent.base.AbsLimitCountExecutor;
import com.hank.corelib.tools.concurrent.base.BaseCyclicBarrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 满足条件的任务执行完成以后，继续接下来执行
 */
public class CyclicBarrierTool extends BaseCyclicBarrier {

	public CyclicBarrierTool(int permits) {
		super(permits);
	}

	@Override
	public Object execute(AbsLimitCountExecutor executor) {
		ExecuteThread thread = new ExecuteThread(executor, this.cyclicBarrier);
		executorService.execute(thread);
		return null;
	}
	private final class ExecuteThread implements Runnable {

		private AbsLimitCountExecutor executor;
		private CyclicBarrier cyclicBarrier;

		@SuppressWarnings("unused")
		public ExecuteThread(AbsLimitCountExecutor executor,
							 CyclicBarrier cyclicBarrier) {
			super();
			this.executor = executor;
			this.cyclicBarrier = cyclicBarrier;
		}

		public void run() {
			// TODO Auto-generated method stub
			try {

				if (executor == null) {
					return;
				}
				executor.execute(executor.getData());

			} catch (Exception e) {
				Logger.e(e);
			} finally {
				try {
					this.cyclicBarrier.await(1000*threadCount,TimeUnit.SECONDS);
					System.out.println("over");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
