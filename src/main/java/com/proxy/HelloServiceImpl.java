package com.proxy;

/**
 * Created by zhaozhengzeng on 2016/12/14.
 */
public class HelloServiceImpl implements HelloService {

    public String hello() {
        return "Hello";
    }

    public String hello(String name) {
        return "Hello," + name;
    }
}
