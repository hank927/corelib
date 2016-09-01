package com.hank.corelib.tools.concurrent;

import com.hank.corelib.tools.concurrent.base.AbsLimitCountExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 封装了CountDownLatchTool，构造时不用提供countDownLatch
 * 作用是对应数量的任务全部完成，才会继续执行
 * created by Hank 
 */

public class CountDownLatchTools {

	private List<AbsLimitCountExecutor> executors;
	private CountDownLatch countDownLatch;
	private CountDownLatchTool countDownLatchUtil;

	public CountDownLatchTools() {
		// TODO Auto-generated constructor stub
	}
	public CountDownLatchTools(List<AbsLimitCountExecutor> executors) {
		this.executors = executors;
	}

	public void AddExecutor(AbsLimitCountExecutor executor) {
		if(this.executors==null){
			this.executors = new ArrayList<AbsLimitCountExecutor>();
		}
		this.executors.add(executor);
	}

	public Object execute() {
		// TODO Auto-generated method stub
		if(executors==null || executors.size()==0) return null;
		countDownLatch = new CountDownLatch(executors.size());
		countDownLatchUtil = new CountDownLatchTool(countDownLatch, executors.size());
		for (AbsLimitCountExecutor executor : executors) {
			countDownLatchUtil.execute(executor);
		}
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
