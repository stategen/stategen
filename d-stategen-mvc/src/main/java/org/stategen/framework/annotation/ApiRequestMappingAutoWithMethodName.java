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
package org.stategen.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import io.swagger.annotations.ResponseHeader;

/**
 * The Interface ApiRequestMappingAutoWithMethodName.
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping()
@ResponseBody
@ApiOperation(value = "")
@Inherited
/***
 * 方法名即路径名，减少硬编码和调试困难
 * 这是一个集成swagger ApiOperation与requestMapping的标注，该类可以可以方法名(method)，方法名自动映射到路径上，减少开发中的不一致
 * @Target({ElementType.METHOD})
 * @Retention(RetentionPolicy.RUNTIME)
 * @RequestMapping()
 * @ResponseBody
 * @ApiOperation(value = "")
 * @Inherited
 * 
 * 
 * @author XiaZhengsheng
 */
public @interface ApiRequestMappingAutoWithMethodName {

    @AliasFor(annotation = RequestMapping.class)
    RequestMethod method() default RequestMethod.POST;

    @AliasFor(annotation = RequestMapping.class)
    String path() default "";

    @AliasFor(annotation = RequestMapping.class)
    String[] params() default {};

    @AliasFor(annotation = RequestMapping.class)
    String[] headers() default {};

    @AliasFor(annotation = RequestMapping.class)
    String produces() default MediaType.APPLICATION_JSON_UTF8_VALUE;

    /***
     * <pre>
     * apiPath值可以为：[(/)(methodName)(/)({PathVariable})),
     * 如 {}｜"delete"｜"/delete"映射路径为:"/delete",
     * {id}|/{id}|delete/{id}|/delete/{id}映射路径为:/delete/{id}
     * 为防止spring根据名称解析，该名不叫 apiPath 重新解析，不@AliasFor(annotation = RequestMapping.class)
     * </pre>
     * @return
     */
    //    String[] apiPath() default {};

    /******************************
     * @ApiOperation
     * ***************************/

    @AliasFor(annotation = ApiOperation.class, attribute = "value")
    String name() default "";

    @AliasFor(attribute = "name")
    String value() default "";

    @AliasFor(annotation = ApiOperation.class)
    String notes() default "";

    @AliasFor(annotation = ApiOperation.class)
    String[] tags() default "";

    @AliasFor(annotation = ApiOperation.class)
    Class<?> response() default Void.class;

    @AliasFor(annotation = ApiOperation.class)
    String responseContainer() default "";

    @AliasFor(annotation = ApiOperation.class)
    String responseReference() default "";

    @AliasFor(annotation = ApiOperation.class)
    @Deprecated
    int position() default 0;

    @AliasFor(annotation = ApiOperation.class)
    String nickname() default "";

    //    @AliasFor(annotation = ApiOperation.class)
    //    String consumes() default "";

    @AliasFor(annotation = ApiOperation.class)
    String protocols() default "";

    Authorization[] authorizations() default @Authorization(value = "");

    @AliasFor(annotation = ApiOperation.class)
    boolean hidden() default false;

    @AliasFor(annotation = ApiOperation.class)
    ResponseHeader[] responseHeaders() default @ResponseHeader(name = "", response = Void.class);

    @AliasFor(annotation = ApiOperation.class)
    int code() default 200;

    @AliasFor(annotation = ApiOperation.class)
    Extension[] extensions() default @Extension(properties = @ExtensionProperty(name = "", value = ""));

}
