package com.proxy;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by zhaozhengzeng on 2017/3/17.
 */
public class Main1 {
    public static void main(String[] args){
        try {
            String hostname = InetAddress.getByName("172.17.5.55").getCanonicalHostName();
            System.out.println(hostname);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
