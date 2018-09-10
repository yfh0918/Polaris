package com.polaris.gateway;

import java.net.InetAddress;

import org.junit.Test;

import com.polaris.http.util.NetUtils;


/**
 * @author:Winning
 *
 * Description:
 *
 */
public class NetUtilsTest {
    @Test
    public  void getLocalAddress() {
        InetAddress inetAddress=NetUtils.getLocalAddress();
        System.out.println(inetAddress.getHostAddress());
        System.out.println(inetAddress.getHostName());
        System.out.println(inetAddress.getCanonicalHostName());
    }

    @Test
    public  void getLocalHost() {
        String localHost=NetUtils.getLocalHost();
        System.out.println(localHost);
    }
}
