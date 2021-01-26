package util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.stategen.framework.util.AssertUtil;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

/***
 * https://blog.csdn.net/ws_54321/article/details/90371876 
 * 
 * @author niaoge
 * @version $Id: ConnectorUtil.java, v 0.1 2021年1月4日 上午10:50:17 XiaZhengsheng Exp $
 */
@Slf4j
public class Net {
    
    private static void bindPort(String host, int port) throws Exception {
        @Cleanup
        Socket s = new Socket();
        s.bind(new InetSocketAddress(host, port));
    }
    
    public static boolean isPortAvailable(int port) {
        try {
            bindPort("0.0.0.0", port);
            bindPort(InetAddress.getLocalHost().getHostAddress(), port);
            if (log.isInfoEnabled()) {
                log.info(String.format("Port %d is free and will be selected!", port));
            }
            return true;
        } catch (Exception e) {
            log.error(String.format("Port %d can't use!", port));
        }
        return false;
    }
    
    public static int findAvailablePort(int start, int end/* 65535 */) {
        int s = start < end ? start : end;
        int e = start > end ? start : end;
        
        for (int port = s; port < e; port++) {
            if (isPortAvailable(port)) {
                if (log.isInfoEnabled()) {
                    log.info(new StringBuilder("port:").append(port).append(" will be selected as a free port").toString());
                }
                return port;
            }
        }
        AssertUtil.throwException(String.format("no port is free from %d to %d", start, end));
        return -1;
    }
    
    /***不能两个系统启动同时太快启动，比如都在竞争8080端口，当同时启动时，两2 app同时发现8080端口没有被占用*/
    public static int from(int start) {
        return findAvailablePort(start, 65535);
    }
    
}