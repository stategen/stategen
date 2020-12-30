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
package org.stategen.framework.spring.mvc;

import java.lang.annotation.Annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.google.common.base.Predicate;

import configs.Configration;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * The Class SwaggerConfig.
 */
@Configuration
@EnableWebMvc
@EnableSwagger2
//@ComponentScan(basePackages ={"com.albert.swagger"})
@Slf4j
public class SwaggerConfig {
    
    private static Class<?> declaringClass(RequestHandler input) {
        return input.getHandlerMethod().getMethod().getDeclaringClass();
    }
    
    /**
     * Predicate that matches RequestHandler with given annotation on the declaring class of the handler method
     *
     * @param annotation - annotation to check
     * @return this
     */
    public static Predicate<RequestHandler> isAnnotated(final Class<? extends Annotation> annotation) {
        return new Predicate<RequestHandler>() {
            
            @Override
            public boolean apply(RequestHandler r) {
                return AnnotatedElementUtils.isAnnotated(declaringClass(r), annotation);
            }

        };
    }
    
    /**
     * Every Docket bean is picked up by the swagger-mvc framework - allowing for multiple
     * swagger groups i.e. same code base multiple swagger resource listings.
     */
    @Bean
    public Docket customDocket() {
        //        return new Docket(DocumentationType.SWAGGER_2);
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(isAnnotated(Api.class))
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .build()
                .enable(Configration.enableSwagger);//<--- Flag to enable or disable possibly loaded using a property file
    }
    

    
}
