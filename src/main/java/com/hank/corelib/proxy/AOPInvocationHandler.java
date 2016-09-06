package com.hank.corelib.proxy;

import com.hank.corelib.logger.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by michael on 15/2/7.
 */
class AOPInvocationHandler implements InvocationHandler {

    private Object proxyObject;

    public AOPInvocationHandler(Object object) {
        proxyObject = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Logger.d("<<AOPInvocationHandler>> invoke method : " + method.getName());

        return  method.invoke(proxyObject, args);

    }

}
