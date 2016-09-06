package com.hank.corelib.proxy;

import com.hank.corelib.logger.Logger;

import java.lang.reflect.Proxy;

/**
 * Created by michael on 15/2/7.
 */
public class ProxyFactory {

    public static final Object newProxyObj(Object proxyObj) {
        if (proxyObj == null) {
            return null;
        }

        Logger.d("<<ProxyFactory::newProxyObj>> make proxy : " + proxyObj.getClass().getName());

        AOPInvocationHandler handler = new AOPInvocationHandler(proxyObj);

        return Proxy.newProxyInstance(handler.getClass().getClassLoader(), proxyObj.getClass().getInterfaces(), handler);
    }

}
