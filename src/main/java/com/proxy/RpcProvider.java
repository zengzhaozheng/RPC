package com.proxy;

/**
 * Created by zhaozhengzeng on 2016/12/14.
 */
public class RpcProvider {
    public static void main(String[] args) throws Exception {
        HelloService service = new HelloServiceImpl();
        RpcFramework.export(service, HelloService.class, 9001);
    }
}
