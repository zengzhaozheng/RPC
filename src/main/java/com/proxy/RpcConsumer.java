package com.proxy;

/**
 * Created by zhaozhengzeng on 2016/12/14.
 */
public class RpcConsumer {
    public static void main(String[] args) throws Exception {
        HelloService service = RpcFramework.refer(HelloService.class, "127.0.0.1", 9001);
        String result = service.hello("rod");
        System.out.println(result);
    }
}















