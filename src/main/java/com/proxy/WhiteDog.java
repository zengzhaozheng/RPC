package com.proxy;

/**
 * Created by zhaozhengzeng on 2016/12/16.
 */
public class WhiteDog implements Dog {
    public void sayHello() {
        System.out.println("Dog say hello");
    }

    public void sayHi() {
        System.out.println("Dog say hi");
    }

    public void saySomething(String something) {
        System.out.println(something);

    }
}
