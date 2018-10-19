package spring.testpackage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.junit.Test;
import org.stategen.framework.util.StringUtil;

public class URLTst {
    
    @Test
    public void decodeCookie() throws UnsupportedEncodingException{
        String cookieValue="%252B852";
        System.out.println("cookieValue<===========>:" + URLDecoder.decode(cookieValue,StringUtil.UTF_8));
    }

}
