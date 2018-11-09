package org.stategen.framework.spring.mvc;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.stategen.framework.util.StringUtil;

public class MultiPathResourceResolver extends PathResourceResolver {
    
    private String trimTo;
    
    public String getTrimTo() {
        return trimTo;
    }
    
    public void setTrimTo(String trimTo) {
        this.trimTo = trimTo;
    }

    @Override
    protected Resource getResource(String resourcePath, Resource location) throws IOException {
        if (StringUtil.isNotBlank(trimTo)){
            resourcePath=StringUtil.trimLeftTo(resourcePath, trimTo);
        }
        return super.getResource(resourcePath, location);
    }
}
