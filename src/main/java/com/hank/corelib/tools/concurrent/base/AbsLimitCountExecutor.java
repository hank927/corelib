package com.hank.corelib.tools.concurrent.base;


public abstract class AbsLimitCountExecutor<T>  {

	private T data;
	
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	
	public AbsLimitCountExecutor() {
		super();
		// TODO Auto-generated constructor stub
	}
	public AbsLimitCountExecutor(T data) {
		super();
		this.data = data;
	}
	public Object execute(T data){
		
		return null;
	}

	public Object execute() {
		// TODO Auto-generated method stub
		return null;
	}
}
