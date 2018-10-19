package spring.testpackage;

import org.springframework.web.bind.annotation.ResponseBody;
import org.stategen.framework.lite.HandleError;

public class BaseAnnimal {
    
    
    @HandleError
    @ResponseBody
    public Object test1(){
        return  null;
    }
}
