package com.github.chenjianhua.common.excel.util;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Slf4j
public class IpUtil {
    public static String getServerIp() {
        InetAddress localHost = null;
        try {
            localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            log.error("获取服务ip失败");
            e.printStackTrace();
        }
        if (null == localHost) {
            return "";
        }
        String hostAddress = localHost.getHostAddress();
        return hostAddress;
    }

    public static void main(String[] args) {
        log.info(IpUtil.getServerIp());
    }
}
