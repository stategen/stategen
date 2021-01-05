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
            return true;
        } catch (Exception e) {
            log.error("Port can't use!", "" + port);
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
    
    public static int from(int start) {
        return findAvailablePort(start, 65535);
    }
    
}