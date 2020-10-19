package com.cetcbigdata.varanus.core.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created with IDEA
 * author:Matthew
 * Date:2019-4-8
 * Time:11:37
 */
@Component
public class CheckNetWork {

    private static final Logger LOG = LoggerFactory.getLogger(CheckNetWork.class);

    /**
     * 监测文档列表获取是否正常
     */
    @Scheduled(cron = "0 0/5 * * * ? ")
    public Boolean checkDocList() throws Exception {
        String ip = "www.baidu.com";
        int port = 80;
        int timeout = 5000;
        Boolean ping = pingHost(ip, port, timeout);
        if (!ping) {
            LOG.warn("第一次尝试ping www.baidu.com 80端口失败");
            ping = pingHost(ip, port, timeout);
        } else {
            return ping;
        }
        if (!ping) {
            LOG.warn("第二次尝试ping www.baidu.com 80端口失败");
            ping = pingHost(ip, port, timeout);
        } else {
            return ping;
        }
        if (!ping) {
            LOG.warn("第三次尝试ping www.baidu.com 80端口失败");
            return false;
        }
        return true;
    }

    public static boolean pingHost(String ip, int port, int timeout) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(ip, port), timeout);
            return true;
        } catch (IOException e) {
            LOG.warn("{} is unconnectable", ip, e.getMessage());
            return false;
        }finally {
            if (socket!=null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    LOG.info("关闭socket异常");
                }
            }
        }
    }
}
