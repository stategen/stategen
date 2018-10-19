package spring.testpackage;

import org.junit.Test;

public class PatternTst {
    
    @Test
    public void testXmlPattern(){
////        
//        String patternStr="^((?!(abc)).)*$";
////        \Qdubbo-facade-auto-\E(?!lottery)\Q.xml\E   <===========>:dubbo-facade-auto-lottery.xml    false
////        \Qdubbo-facade-auto-\E(?!lottery)\Q.xml\E   <===========>:dubbo-facade-manual-lvyou.xml    false
//        String lotteryPath ="dubbo-facade-auto-lottery.xml";
//        String lvyouPath ="dubbo-facade-auto-lvyou.xml";
        
        String reg= "http://hugh-wangp\\.iteye\\.com/(?!.*(weibo)).*";    
        
        System.out.println("http://hugh-wangp.iteye.com/".matches(reg));//通过  
        System.out.println("http://hugh-wangp.iteye.com/blog".matches(reg));//通过                         
        System.out.println("http://hugh-wangp.iteye.com/blog/guest_book".matches(reg));//通过  
        System.out.println("http://hugh-wangp.iteye.com/weibo".matches(reg));//不通过  
        System.out.println("http://hugh-wangp.iteye.com/link".matches(reg));//不通过  
//        \Qdubbo-facade-auto-\E(?!.*(lottery)).*\Q.xml\E   <===========>:dubbo-facade-auto-lottery.xml    false
//                          \\Qdubbo-facade-auto-\\E(?!.*(lottery)).*\\Q.xml\\E   <===========>:dubbo-facade-auto-lvyou.xml    true   false
        String patternStr="\\Qdubbo-facade-auto-\\E(?!.*(lottery)).*\\Q.xml\\E";
        
        System.out.println("Qdubbo-facade-auto-lottery.xml".matches(patternStr) );
        System.out.println("dubbo-facade-auto-lvyou.xml".matches(patternStr) );
        
        
    }
    

}
