package com.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by zhaozhengzeng on 2016/12/16.
 */
public class Main {
    public static void main(String[] args) {
        //被代理对象
        final WhiteDog whiteDog = new WhiteDog();

        Dog dog = (Dog) Proxy.newProxyInstance(whiteDog.getClass().getClassLoader(), whiteDog.getClass().getInterfaces(), new InvocationHandler() {
            Object result;

            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals("saySomething")) {
                    System.out.println("invoke saySomething");
                    result = method.invoke(whiteDog, args);
                } else {
                    System.out.println("before....." + method.getName());
                    result = method.invoke(whiteDog, args);
                    System.out.println("aflter....." + method.getName());
                }
                return result;
            }

        });
        dog.sayHello();
        dog.sayHi();
        dog.saySomething("xxxxx");


    }
}
