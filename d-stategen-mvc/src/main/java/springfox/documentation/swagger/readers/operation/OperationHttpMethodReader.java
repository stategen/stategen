/*
 * Copyright (C) 2018  niaoge<78493244@qq.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package springfox.documentation.swagger.readers.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.stategen.framework.util.CollectionUtil;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

/**
 * The Class OperationHttpMethodReader.
 */
@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class OperationHttpMethodReader implements OperationBuilderPlugin {
    private static final Logger log = LoggerFactory.getLogger(OperationHttpMethodReader.class);

    @Override
    public void apply(OperationContext context) {
        HandlerMethod handlerMethod = context.getHandlerMethod();

        ApiOperation apiOperationAnnotation = handlerMethod.getMethodAnnotation(ApiOperation.class);
        HttpMethod httpMethod = null;
        if (apiOperationAnnotation != null && StringUtils.hasText(apiOperationAnnotation.httpMethod())) {
            String apiMethod = apiOperationAnnotation.httpMethod();
            try {
                RequestMethod.valueOf(apiMethod);
            } catch (IllegalArgumentException e) {
                log.error("Invalid http method: " + apiMethod + "Valid ones are [" + RequestMethod.values() + "]", e);
            }
            httpMethod = HttpMethod.valueOf(apiMethod);
        } else {
            RequestMapping requestMapping = handlerMethod.getMethodAnnotation(RequestMapping.class);
            RequestMethod[] methods = requestMapping.method();
            if (CollectionUtil.isNotEmpty(methods)) {
                httpMethod = HttpMethod.valueOf(CollectionUtil.getFirst(methods).toString());
            }
        }

        if (httpMethod == null) {
            httpMethod = HttpMethod.GET;
        }

        context.operationBuilder().method(httpMethod);
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return SwaggerPluginSupport.pluginDoesApply(delimiter);
    }
}
