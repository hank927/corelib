package com.hank.corelib.tools.concurrent;

import com.hank.corelib.tools.concurrent.base.AbsLimitCountExecutor;
import com.hank.corelib.tools.concurrent.base.BaseCountDownLatch;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchTool extends BaseCountDownLatch {

	public CountDownLatchTool(CountDownLatch countDownLatch,int count) {
		super(countDownLatch,count);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object execute(AbsLimitCountExecutor executor) {
		ExecuteThread thread = new ExecuteThread(executor, countDownLatch);
		executorService.execute(thread);
		return null;
	}

	private final class ExecuteThread implements Runnable {

		private AbsLimitCountExecutor executor;
		private CountDownLatch countDownLatch;


		public ExecuteThread(AbsLimitCountExecutor executor,
							 CountDownLatch countDownLatch) {
			super();
			this.executor = executor;
			this.countDownLatch = countDownLatch;
		}

		public void run() {
			// TODO Auto-generated method stub
			try {

				if (executor == null) {
					return;
				}
				executor.execute(executor.getData());

			} catch (Exception e) {
				// TODO: handle exception
			}finally{
				//System.out.println(this.countDownLatch.getCount());
				this.countDownLatch.countDown();
				//System.out.println(this.countDownLatch.getCount());
			}
		}
	}
}
