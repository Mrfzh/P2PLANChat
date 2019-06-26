package com.feng.p2planchat.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/7
 */
public class IpAddressUtil {

    private static final String TAG = "fzh";

    /**
     * 获取本机IPv4地址
     *
     * @param context
     * @return 本机IPv4地址；null：无网络连接
     */
    public static String getIpAddress(Context context) {
        // 获取WiFi服务
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // 判断WiFi是否开启
        if (wifiManager.isWifiEnabled()) {
            // 已经开启了WiFi
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            return intToIp(ipAddress);
        } else {
            //返回未开启WiFi时的ip地址
            return getHostIP();
        }
    }

    private static String intToIp(int ipAddress) {
        return (ipAddress & 0xFF) + "." +
                ((ipAddress >> 8) & 0xFF) + "." +
                ((ipAddress >> 16) & 0xFF) + "." +
                (ipAddress >> 24 & 0xFF);
    }

    private static String getHostIP() {
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return hostIp;
    }


    /**
     * 获取其他在线用户的ip地址
     *
     * @return
     */
    public static List<String> getIpAddressList(Context context) {
        final List<String> ipAddressList = new ArrayList<>();

        final String ipAddr = getIpAddress(context);
        final String prefix = ipAddr.substring(0, ipAddr.lastIndexOf(".") + 1);
        final AtomicInteger atomicInteger = new AtomicInteger(0);

        //创建256个线程分别去ping
        for ( int i = 0; i < 256; i++) {
            final int finalI = i;
            new Thread(new Runnable() {
                public void run() {
                    //利用ping命令判断
                    String current_ip = prefix + finalI;
                    //要执行的ping命令，其中 -c 为发送的次数，-w 表示发送后等待响应的时间
                    String ping = "ping -c 4 -w 5 " + current_ip;
                    Process proc = null;
                    try {
                        proc = Runtime.getRuntime().exec(ping);
                        int result = proc.waitFor();
                        if (result == 0 && !current_ip.equals(ipAddr)) {
                            ipAddressList.add(current_ip);
                            Log.d(TAG, current_ip + ": true");
                        }
                        atomicInteger.incrementAndGet();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    } finally {
                        if (proc != null) {
                            proc.destroy();
                        }
                    }
                }
            }).start();
        }

        while (atomicInteger.get() < 256) {

        }

        return ipAddressList;
    }

}
