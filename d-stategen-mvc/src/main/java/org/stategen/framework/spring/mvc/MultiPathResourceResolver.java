package org.stategen.framework.spring.mvc;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.stategen.framework.util.StringUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class MultiPathResourceResolver extends PathResourceResolver {
    
    private String trimTo;
    
    @Override
    protected Resource getResource(String resourcePath, Resource location) throws IOException {
        if (StringUtil.isNotEmpty(trimTo)) {
            String oldResourcePath = resourcePath;
            resourcePath = StringUtil.trimLeftFormRightTo(oldResourcePath, trimTo);
            
            if (log.isDebugEnabled()) {
                log.debug(new StringBuilder("输出debug信息:").append("\n MultiPathResourceResolver use trimTo property:")
                        .append(trimTo).append("\nbefore: oldResourcePath:").append(oldResourcePath).append("\nresourcePath:")
                        .append(resourcePath).append("\nlocation:").append(location).toString());
            }
            
        }
        return super.getResource(resourcePath, location);
    }
}
